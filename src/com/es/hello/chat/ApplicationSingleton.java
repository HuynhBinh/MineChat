package com.es.hello.chat;

import java.util.ArrayList;
import java.util.List;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.users.model.QBUser;

public class ApplicationSingleton
{

    public static String CURRENT_GROUP_NAME = "";

    public static String CURRENT_GROUP_PHOTO = "";

    public static List<QBUser> currentSelectedUsersInGroup = new ArrayList<QBUser>();

    public static List<QBDialog> selectedDialogToCreateGroupChat = new ArrayList<QBDialog>();

    public static boolean isNewGroupPhoto = false;

    // public static Queue queueActivities = new Queue();

    // public static QBUser currentUser;

    // public static Map<Integer, QBUser> dialogsUsers = new HashMap<Integer,
    // QBUser>();

    // public static String USER_LOGIN = "";

    // public static String USER_PASSWORD = "";

    /*public static QBUser getCurrentUser()
    {

    return ApplicationSingleton.currentUser;
    }*/

    /*public static void setCurrentUser(QBUser currentUser)
    {

    ApplicationSingleton.currentUser = currentUser;
    }*/

    /* public static Map<Integer, QBUser> getDialogsUsers()
     {

    return ApplicationSingleton.dialogsUsers;
     }*/

    /*public static void setDialogsUsers(List<QBUser> setUsers)
    {

    ApplicationSingleton.dialogsUsers.clear();

    for (QBUser user : setUsers)
    {
        ApplicationSingleton.dialogsUsers.put(user.getId(), user);
    }
    }*/

    /* public static void addDialogsUsers(List<QBUser> newUsers)
     {

    for (QBUser user : newUsers)
    {
        ApplicationSingleton.dialogsUsers.put(user.getId(), user);
    }
     }*/

}
