package com.example.personalfinance.error;

public class MessageCode {
    //input
    public static final String field_title_required = "Empty title, please try again";
    public static final String field_title_char_length_allow = "Title must contain at least 5 and can not exceed 20 characters, please try again";
    public static final String field_title_alpha_only = "Title can only contain alphabet, please try again ";
    public static final String field_title_duplicated = "This title has existed, please try again";

    public static final double amount_limit = 1e9; //usd

    public static final String field_amount_required = "Number field must not be empty";
    public static final String field_amount_limit_allow = "Number must greater than 0 and can not exceed 1 billion usd, please try again";

    public static final String field_description_max_char = "Description field length exceeded 40 characters, please try again";

    public static final String from_date_field_constraint = "Begin date must before end date, please try again !";
    public static final String to_date_field_constraint = "End date must after begin date, please try again !";

    //crud
    public static final String success_creation = "Creating successfully";
    public static final String fail_creation = "Creating failed";
    public static final String success_updation = "Update successfully";
    public static final String fail_updation = "Updating failed";
    public static final String success_deletion = "Delete successfully";
    public static final String fail_deletion = "Delete failed";

    //transaction
    public static final String wallet_amount_not_sufficient = "Wallet amount not sufficient to create transaction !";
    public static final String transaction_deletion_restriction = "Only allowing deletion of the latest transaction !";
    public static final String bill_missing_item = "Please add item to this bill";
    //category
    public static final String category_duplicated = "This category has existed, please try again";
    public static final String category_not_match_type = "Category mismatch ";
    public static final String category_required = "Please pick category";
    public static final String use_category = "This category has at least 1 transaction or budget using, can not modify";
    //budget
    public static final String budget_non_existed = "This budget is not existed anymore";
    //filter
    public static final String success_reset = "Reset successfully";
}
