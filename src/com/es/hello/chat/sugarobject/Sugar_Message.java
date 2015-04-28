package com.es.hello.chat.sugarobject;

import com.orm.SugarRecord;

public class Sugar_Message extends SugarRecord<Sugar_Message>
{

    public String messId;

    public String messChatDialogId;

    public String messMessage;

    public long messDateSent;

    public int messSenderId;

    public int messRecipientId;

    public int messRead;

    public String messAttachment;

    public String messCustomParam;

    public String isShowDate;

    public Sugar_Message()
    {

    }

}
