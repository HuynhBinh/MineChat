package com.es.hello.chat.sugarobject;

import com.orm.SugarRecord;

public class Sugar_Dialog extends SugarRecord<Sugar_Dialog>
{

    public String dialogId;

    public int dialogCreatedUserID;

    public int dialogType;

    public String dialogName;

    public String dialogPhoto;

    public String dialogXmppRoomJid;

    public String dialogOccupantsIds;

    public String dialogLastMessage;

    public long dialogLastMessageDateSent;

    public int dialogLastMessageUserId;

    public int dialogUnreadMessagesCount;

    public boolean isDownloadedMessOfDialog;

    public int dialogStatus; // 3 leave group

    public Sugar_Dialog()
    {

    }

}
