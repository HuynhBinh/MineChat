package com.es.hello.chat.sugarobject;

import com.orm.SugarRecord;

public class Sugar_User extends SugarRecord<Sugar_User>
{

    public int userId;

    public String userLogin;

    public String userPassword;

    public String userWebsite;

    public String userFullName;

    public String userCustomData;

    public Sugar_User()
    {

    }

}
