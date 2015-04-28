package com.es.hello.chat.core;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import android.util.Log;
import android.widget.Toast;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.ui.activities.ChatActivity;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBParticipantListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;

public class GroupChatManagerImpl extends QBMessageListenerImpl<QBGroupChat> implements ChatManager, QBParticipantListener
{

    private static final String TAG = "GroupChatManagerImpl";

    private ChatActivity chatActivity;

    private QBGroupChatManager groupChatManager;

    private QBGroupChat groupChat;

    private QBParticipantListener participantListener;

    public GroupChatManagerImpl(ChatActivity chatActivity)
    {

	this.chatActivity = chatActivity;
	groupChatManager = QBChatService.getInstance().getGroupChatManager();

    }

    @SuppressWarnings("rawtypes")
    public void joinGroupChat(QBDialog dialog, QBEntityCallback callback)
    {

	String roomJid = dialog.getRoomJid();

	if (roomJid != null)
	{
	    if (groupChatManager != null)
	    {
		groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());

		join(groupChat, callback);
	    }
	    else
	    {
		groupChatManager = QBChatService.getInstance().getGroupChatManager();
		groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());
		join(groupChat, callback);
	    }

	}
	else
	{
	    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(false, chatActivity);
	    Toast.makeText(chatActivity, "Join group error. Please try again!", Toast.LENGTH_LONG).show();
	}

    }

    @SuppressWarnings("rawtypes")
    private void join(final QBGroupChat groupChat, final QBEntityCallback callback)
    {

	DiscussionHistory history = new DiscussionHistory();
	history.setMaxStanzas(0);

	groupChat.join(history, new QBEntityCallbackImpl()
	{

	    @Override
	    public void onSuccess()
	    {

		groupChat.addMessageListener(GroupChatManagerImpl.this);

		chatActivity.runOnUiThread(new Runnable()
		{

		    @Override
		    public void run()
		    {

			callback.onSuccess();

		    }
		});
		Log.w("Chat", "Join successful");

	    }

	    @Override
	    public void onError(final List list)
	    {

		chatActivity.runOnUiThread(new Runnable()
		{

		    @SuppressWarnings("unchecked")
		    @Override
		    public void run()
		    {

			callback.onError(list);
		    }
		});

		Log.w("Could not join chat, errors:", Arrays.toString(list.toArray()));
	    }
	});
    }

    @Override
    public void release() throws XMPPException
    {

	if (groupChat != null)
	{
	    try
	    {
		groupChat.leave();
	    }
	    catch (SmackException.NotConnectedException nce)
	    {
		nce.printStackTrace();
	    }

	    groupChat.removeMessageListener(this);
	}
    }

    @Override
    public void sendMessage(QBChatMessage message) throws Exception // throws
								    // XMPPException,
    // SmackException.NotConnectedException
    {

	// String dia = message.getProperty("dialog_id");
	// String sender = message.getProperty("sender_id");
	// String recipient = message.getProperty("recipient_id");
	// String messBody = message.getBody();
	// String messID = message.getId();

	// PrivateChatManagerImpl.saveMessageToDB(message, messID,
	// Integer.parseInt(sender), Integer.parseInt(recipient), dia,
	// messBody);

	// String dia = message.getProperty("dialog_id");
	// String sender = message.getProperty("sender_id");
	// String recipient = message.getProperty("recipient_id");
	// String messBody = message.getBody();
	// String messID = message.getId();

	if (groupChat != null)
	{

	    groupChat.sendMessage(message);

	}

    }

    @Override
    public void processMessage(QBGroupChat groupChat, QBChatMessage message)
    {

	StaticFunction.saveMessageToDB(message);
	chatActivity.showMessageOnReceiveOnly(message);

    }

    @Override
    public void processMessageDelivered(QBGroupChat sender, String messageID)
    {

	super.processMessageDelivered(sender, messageID);
    }

    @Override
    public void processMessageRead(QBGroupChat sender, String messageID)
    {

	super.processMessageRead(sender, messageID);
    }

    @Override
    public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage)
    {

    }

    @Override
    public void processPresence(QBGroupChat arg0, QBPresence arg1)
    {

	Log.e("processPresence", "processPresence");

    }
}
