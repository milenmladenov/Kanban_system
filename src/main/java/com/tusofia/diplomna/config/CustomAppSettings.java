package com.tusofia.diplomna.config;


import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomAppSettings {

    @Autowired
    UserService userService;

    public static final int MAXIMUM_BUG_REPORTS = 10;
    public static final int MAXIMUM_INCOME_AND_EXPENSE_AMOUNT = 90000000;
    public static final String EVENT_TODO_COLOUR = "#E57373";





}
