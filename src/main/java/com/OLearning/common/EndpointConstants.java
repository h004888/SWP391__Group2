package com.OLearning.common;

public class EndpointConstants {
    // Login/Authentication
    public static final String LOGIN_PAGE = "loginPage/normalLogin/login";
    public static final String ADMIN_LOGIN_PAGE = "loginPage/adminLogin/sign-in";
    public static final String ADMIN_SIGNUP_PAGE = "loginPage/adminLogin/sign-up";
    public static final String SIGNUP_PAGE = "loginPage/normalLogin/signup";
    public static final String FORGOT_PASSWORD_PAGE = "loginPage/normalLogin/forgotPassword";
    public static final String OTP_VERIFICATION_PAGE = "loginPage/normalLogin/otpVerification";
    public static final String RESET_PASSWORD_PAGE = "loginPage/normalLogin/resetPassword";
    public static final String CHANGE_PASSWORD_OAUTH2_PAGE = "loginPage/normalLogin/changePassword-Oauth2";
    public static final String ERROR_403_PAGE = "loginPage/403";

    // Error
    public static final String ERROR_404_PAGE = "error/404";
    public static final String ERROR_GLOBAL_PAGE = "error/global-error";

    // Admin Dashboard - Main
    public static final String ADMIN_DASHBOARD_INDEX = "adminDashBoard/index";
    public static final String ADMIN_DASHBOARD_FRAGMENT_CONTENT = "fragmentContent";

    // Business Rule
    public static final String ADMIN_BUSINESS_RULE_PAGE = "/admin/businessRule";
    public static final String ADMIN_BUSINESS_RULE_FRAGMENT = "adminDashBoard/fragments/businessRuleContent :: businessRuleContent";
    public static final String ADMIN_BUSINESS_RULE_REDIRECT = "redirect:/admin/businessRule";

    // Category
    public static final String ADMIN_CATEGORY_PAGE = "/admin/category";
    public static final String ADMIN_CATEGORY_FRAGMENT = "adminDashBoard/fragments/category :: categoryList";
    public static final String ADMIN_CATEGORY_TABLE_ROW_FRAGMENT = "adminDashBoard/fragments/category :: categoryTableRowContent";
    public static final String ADMIN_CATEGORY_PAGINATION_FRAGMENT = "adminDashBoard/fragments/category :: categoryPagination";
    public static final String ADMIN_CATEGORY_EDIT_FRAGMENT = "adminDashBoard/fragments/category :: editCategory";
    public static final String ADMIN_CATEGORY_COURSE_LIST_FRAGMENT = "adminDashBoard/fragments/category :: courseListContent";

    // User/Account
    public static final String ADMIN_ACCOUNT_FRAGMENT = "adminDashBoard/fragments/accountContent :: accountContent";
    public static final String ADMIN_ACCOUNT_DETAIL_FRAGMENT = "adminDashBoard/fragments/accountDetailContent :: accountDetail";
    public static final String ADMIN_ACCOUNT_PAGINATION_FRAGMENT = "adminDashBoard/fragments/userTableRowContent :: accountPagination";
    public static final String ADMIN_ACCOUNT_TABLE_ROW_FRAGMENT = "adminDashBoard/fragments/userTableRowContent :: userTableRows";
    public static final String ADMIN_ACCOUNT_DETAIL_ENROLLED_FRAGMENT = "adminDashBoard/fragments/accountDetailContent :: enrolledCourseListFragment";

    // Instructor
    public static final String ADMIN_INSTRUCTOR_LIST_FRAGMENT = "adminDashBoard/fragments/instructorListContent :: instructorListContent";
    public static final String ADMIN_INSTRUCTOR_DETAIL_FRAGMENT = "adminDashBoard/fragments/instructorDetailContent :: instructorDetailContent";
    public static final String ADMIN_INSTRUCTOR_COURSE_LIST_FRAGMENT = "adminDashBoard/fragments/instructorDetailContent :: courseListFragment";
    public static final String ADMIN_INSTRUCTOR_REVIEW_TABLE_FRAGMENT = "adminDashBoard/fragments/instructorDetailContent :: reviewTableFragment";
    public static final String ADMIN_INSTRUCTOR_REQUEST_FRAGMENT = "adminDashBoard/fragments/instructorRequestContent :: instructorRequestContent";

    // Course
    public static final String ADMIN_COURSE_FRAGMENT = "adminDashBoard/fragments/courseContent :: courseContent";
    public static final String ADMIN_COURSE_TABLE_ROW_FRAGMENT = "adminDashBoard/fragments/courseTableRowContent :: courseTableRowContent";
    public static final String ADMIN_COURSE_PAGINATION_FRAGMENT = "adminDashBoard/fragments/courseTableRowContent :: coursePagination";
    public static final String ADMIN_COURSE_DETAIL_FRAGMENT = "adminDashBoard/fragments/courseDetailContent :: courseDetail";

    // Course Maintenance
    public static final String ADMIN_COURSE_MAINTENANCE_FRAGMENT = "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances";
    public static final String ADMIN_COURSE_MAINTENANCE_TABLE_ROW_FRAGMENT = "adminDashBoard/fragments/courseMaintenanceTableRowContent :: maintenanceTableRowContent";
    public static final String ADMIN_COURSE_MAINTENANCE_PAGINATION_FRAGMENT = "adminDashBoard/fragments/courseMaintenanceTableRowContent :: maintenancePagination";

    // Orders
    public static final String ADMIN_ORDERS_FRAGMENT = "adminDashBoard/fragments/ordersContent :: contentOrders";
    public static final String ADMIN_ORDERS_TABLE_ROW_FRAGMENT = "adminDashBoard/fragments/ordersTableRowContent :: ordersTableRowContent";
    public static final String ADMIN_ORDERS_PAGINATION_FRAGMENT = "adminDashBoard/fragments/ordersTableRowContent :: ordersPagination";
    public static final String ADMIN_ORDER_DETAILS_FRAGMENT = "adminDashBoard/fragments/orderDetailsContent :: contentOrderDetails";

 
} 