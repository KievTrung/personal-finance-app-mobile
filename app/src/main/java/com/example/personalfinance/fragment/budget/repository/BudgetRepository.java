package com.example.personalfinance.fragment.budget.repository;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.BudgetCategoryDao;
import com.example.personalfinance.datalayer.local.daos.BudgetDao;
import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.dataclass.BudgetWithCategories;
import com.example.personalfinance.datalayer.local.entity.Budget;
import com.example.personalfinance.datalayer.local.entity.BudgetCategory;
import com.example.personalfinance.datalayer.local.entity.auxiliry.DeletedRow;
import com.example.personalfinance.datalayer.local.enums.SyncState;
import com.example.personalfinance.datalayer.workers.SyncStateManager;
import com.example.personalfinance.fragment.budget.model.BudgetModel;
import com.example.personalfinance.fragment.category.model.CategoryModel;
import com.example.personalfinance.fragment.category.repository.CategoryRepository;
import com.example.personalfinance.fragment.transaction.model.Filter;
import com.example.personalfinance.fragment.transaction.model.TransactModel;
import com.example.personalfinance.fragment.transaction.repository.TransactRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BudgetRepository {
    private final BudgetDao budgetDao;
    private final BudgetCategoryDao budgetCategoryDao;
    private final DeletedRowDao deletedRowDao;

    public BudgetRepository(Context context){
        AppLocalDatabase app = AppLocalDatabase.getInstance(context);
        budgetDao = app.getBudgetDao();
        budgetCategoryDao = app.getBudgetCategoryDao();
        deletedRowDao = app.getDeletedRowDao();
    }

    public Completable insertBudget(BudgetModel budget) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    //insert budget
                    long id = budgetDao.insertBudget(toBudget(budget, SyncStateManager.Action.insert, null));

                    //insert budget category
                    for (CategoryModel category : budget.getCategories()){
                        budgetCategoryDao.insert(new BudgetCategory(category.getId(), (int)id, SyncStateManager.determineSyncState(SyncStateManager.Action.insert, null)));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteBudget(BudgetModel budgetModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    SyncState currentState = budgetDao.getState(budgetModel.getId());
                    if (currentState == SyncState.synced)
                        deletedRowDao.insert(new DeletedRow(budgetModel.getId(), DeletedRow.Table.budget));
                    budgetDao.deleteBudget(budgetModel.getId());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateBudget(BudgetModel budgetModel) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    //update transact
                    SyncState currentStateTransact = budgetDao.getState(budgetModel.getId());
                    budgetDao.updateBudget(toBudget(budgetModel, SyncStateManager.Action.update, currentStateTransact));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteCategory(Integer budgetId, Integer categoryId) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    SyncState currentState = budgetCategoryDao.getState(budgetId, categoryId);
                    if (currentState == SyncState.synced)
                        deletedRowDao.insert(new DeletedRow(budgetId, categoryId, DeletedRow.Table.budgetCategory));
                    budgetCategoryDao.delete(budgetId, categoryId);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addCategory(Integer budgetId, Integer categoryId) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> budgetCategoryDao.insert(new BudgetCategory(categoryId, budgetId, SyncState.not_sync_insert)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<TransactModel>> getAllTransacts(Integer budgetId){
        return budgetDao
                .getAllTransacts(budgetId)
                .map(transactWithCategories -> {
                    List<TransactModel> transactModels = new ArrayList<>();
                    transactWithCategories.forEach(transactWithCategory -> {
                        transactModels.add(TransactRepository.toTransactModel(transactWithCategory.transact, transactWithCategory.category));
                    });
                    return transactModels;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<List<BudgetModel>> getAllBudget(Filter filter){
        return budgetDao
                //get the budget between from and to date
                .getAllBudget()
                .map(budgetsWithCategories -> {
                    List<BudgetModel> budgets = new ArrayList<>();
                    for (BudgetWithCategories budgetWithCategories1 : budgetsWithCategories){
                        //filter the title
                        if (!filter.title.isEmpty() && !budgetWithCategories1.budget.getBudget_title().contains(filter.title)) continue;
                        //create new entry in budget
                        BudgetModel entry = toBudgetModel(budgetWithCategories1.budget);
                        //set category for budget entry
                        List<CategoryModel> categories = new ArrayList<>();
                        budgetWithCategories1.categories.forEach(category -> categories.add(CategoryRepository.toCategoryModel(category)));
                        entry.setCategories(categories);
                        //filter the category
                        if (!filter.categories.isEmpty() && !new HashSet<>(entry.getCategories()).containsAll(filter.categories)) continue;
                        //add the entry to budgets
                        budgets.add(entry);
                    }
                    return budgets;
                })
                //cacualte total transact amount for each budget
                .flattenAsObservable(budgetModels -> budgetModels)
                .flatMapSingle(budgetModel -> {
                    budgetModel.setTotalTransactsAmount(budgetDao.totalTransactsAmount(budgetModel.getId()));
                    return Single.just(budgetModel);
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<BudgetModel> getBudget(Integer budgetId){
        return budgetDao
                .getBudget(budgetId)
                .map(budgetWithCategories -> {
                    BudgetModel budgetModel = toBudgetModel(budgetWithCategories.budget);

                    //set category for budget entry
                    List<CategoryModel> categories = new ArrayList<>();
                    budgetWithCategories.categories.forEach(category -> categories.add(CategoryRepository.toCategoryModel(category)));
                    budgetModel.setCategories(categories);

                    //cacualte total transact amount for each budget
                    budgetModel.setTotalTransactsAmount(budgetDao.totalTransactsAmount(budgetModel.getId()));

                    return budgetModel;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> getBudgetId(String title){
        return budgetDao
                .getBudgetId(title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Budget toBudget(BudgetModel budgetModel, SyncStateManager.Action action, SyncState currentState){
        Budget budget = new Budget();
        budget.setBudget_id(budgetModel.getId());
        budget.setBudget_title(budgetModel.getBudget_title());
        budget.setBudget_amount(budgetModel.getBudget_amount());
        budget.setStart_date(budgetModel.getStart_date());
        budget.setEnd_date(budgetModel.getEnd_date());
        budget.setType(budgetModel.getType());
        budget.setSyncState(SyncStateManager.determineSyncState(action, currentState));
        return budget;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public BudgetModel toBudgetModel(Budget budget){
        BudgetModel budgetModel = new BudgetModel();
        budgetModel.setId(budget.getBudget_id());
        budgetModel.setBudget_title(budget.getBudget_title());
        budgetModel.setBudget_amount(budget.getBudget_amount());
        budgetModel.setStart_date(budget.getStart_date());
        budgetModel.setEnd_date(budget.getEnd_date());
        budgetModel.setType(budget.getType());
        return budgetModel;
    }

}
