package com.es.hello.chat.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.ui.activities.Activity_Setting;
import com.es.hello.chat.ui.activities.ChatActivity;
import com.es.hello.chat.ui.adapters.UsersAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lat.hello.chat.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class UsersFragment extends Fragment implements QBEntityCallback<ArrayList<QBUser>>
{

    private static final int PAGE_SIZE = 20;

    private PullToRefreshListView usersList;

    private Button createChatButton;

    private int listViewIndex;

    private int listViewTop;

    private RelativeLayout progressBar;

    private UsersAdapter usersAdapter;

    private int currentPage = 0;

    private List<QBUser> users = new ArrayList<QBUser>();

    ListView listview;

    public List<QBUser> selectedUser = new ArrayList<QBUser>();

    public static String GroupName = "";

    public static UsersFragment getInstance()
    {

	return new UsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

	View v = inflater.inflate(R.layout.fragment_users, container, false);
	usersList = (PullToRefreshListView) v.findViewById(R.id.usersList);
	progressBar = (RelativeLayout) v.findViewById(R.id.progressBarRelative);
	createChatButton = (Button) v.findViewById(R.id.createChatButton);
	createChatButton.setOnClickListener(new View.OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		if (usersAdapter.getSelected().size() == 1)
		{
		    // ApplicationSingleton.addDialogsUsers(usersAdapter.getSelected());

		    // Create new group dialog
		    //
		    QBDialog dialogToCreate = new QBDialog();
		    dialogToCreate.setName(usersListToChatName());

		    dialogToCreate.setType(QBDialogType.PRIVATE);

		    dialogToCreate.setOccupantsIds(StaticFunction.getUserIds1(usersAdapter.getSelected()));

		    QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
		    {

			@Override
			public void onSuccess(QBDialog dialog, Bundle args)
			{

			    startSingleChat(dialog);

			}

			@Override
			public void onError(List<String> errors)
			{

			    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			    dialog.setMessage("dialog creation errors: " + errors).create().show();
			}
		    });

		}
		else
		{
		    createDialogGroupName(getActivity());
		}

	    }
	});

	usersList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
	{

	    @Override
	    public void onRefresh(PullToRefreshBase<ListView> refreshView)
	    {

		// Do work to refresh the list here.
		loadNextPage();
		listViewIndex = usersList.getRefreshableView().getFirstVisiblePosition();
		View v = usersList.getRefreshableView().getChildAt(0);
		listViewTop = (v == null) ? 0 : v.getTop();
	    }
	});

	listview = usersList.getRefreshableView();
	listview.setOnItemClickListener(new OnItemClickListener()
	{

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	    {

		// selectedUser = new ArrayList<QBUser>();
		// selectedUser.add(usersAdapter.dataSource.get(position));

		progressBar.setVisibility(View.VISIBLE);

		usersAdapter.selected = new ArrayList<QBUser>();
		usersAdapter.selected.add(usersAdapter.dataSource.get(position - 1));

		QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(getActivity());

		if (currentLoginUser.getLogin().equalsIgnoreCase(usersAdapter.dataSource.get(position - 1).getLogin()))
		{
		    Intent intent = new Intent(getActivity(), Activity_Setting.class);
		    startActivity(intent);

		    return;
		}

		// Create new group dialog
		//
		QBDialog dialogToCreate = new QBDialog();
		dialogToCreate.setName(usersListToChatName());
		if (usersAdapter.getSelected().size() == 1)
		{
		    dialogToCreate.setType(QBDialogType.PRIVATE);
		    String avatar = usersAdapter.getSelected().get(0).getWebsite();
		    if (avatar != null && !avatar.equals(""))
		    {
			dialogToCreate.setPhoto(avatar);
		    }

		}

		dialogToCreate.setOccupantsIds(StaticFunction.getUserIds1(usersAdapter.getSelected()));

		QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
		{

		    @Override
		    public void onSuccess(QBDialog dialog, Bundle args)
		    {

			ArrayList<QBDialog> listDialogs = new ArrayList<QBDialog>();
			listDialogs.add(dialog);

			StaticFunction.saveListDialogToDB(listDialogs);

			if (usersAdapter.getSelected().size() == 1)
			{
			    startSingleChat(dialog);
			}

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage("dialog creation errors: " + errors).create().show();
		    }
		});

	    }

	});

	loadNextPage();
	return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {

	super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	switch (item.getItemId())
	{

	    case R.id.action_create_chat:

		if (usersAdapter.getSelected().size() == 1)
		{
		    // ApplicationSingleton.addDialogsUsers(usersAdapter.getSelected());

		    // Create new group dialog
		    //
		    QBDialog dialogToCreate = new QBDialog();
		    dialogToCreate.setName(usersListToChatName());

		    dialogToCreate.setType(QBDialogType.PRIVATE);

		    dialogToCreate.setOccupantsIds(StaticFunction.getUserIds1(usersAdapter.getSelected()));

		    QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
		    {

			@Override
			public void onSuccess(QBDialog dialog, Bundle args)
			{

			    startSingleChat(dialog);

			}

			@Override
			public void onError(List<String> errors)
			{

			    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			    dialog.setMessage("dialog creation errors: " + errors).create().show();
			}
		    });

		}
		else
		{
		    createDialogGroupName(getActivity());
		}

		return true;
	    default:
		break;
	}

	return false;

    }

    public static QBPagedRequestBuilder getQBPagedRequestBuilder(int page)
    {

	QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
	pagedRequestBuilder.setPage(page);
	pagedRequestBuilder.setPerPage(PAGE_SIZE);

	return pagedRequestBuilder;
    }

    @Override
    public void onSuccess(ArrayList<QBUser> newUsers, Bundle bundle)
    {

	// save users
	//
	users.addAll(newUsers);

	// Prepare users list for simple adapter.
	//
	usersAdapter = new UsersAdapter(users, getActivity());
	usersList.setAdapter(usersAdapter);
	usersList.onRefreshComplete();
	usersList.getRefreshableView().setSelectionFromTop(listViewIndex, listViewTop);

	progressBar.setVisibility(View.GONE);

	//
	StaticFunction.saveSugarUserToDB(newUsers);
	//

    }

    @Override
    public void onSuccess()
    {

    }

    @Override
    public void onError(final List<String> errors)
    {

	if (UsersFragment.getInstance() != null)
	{
	    if (UsersFragment.getInstance().getActivity() != null)
	    {
		if (!UsersFragment.getInstance().getActivity().isFinishing())
		{
		    UsersFragment.getInstance().getActivity().runOnUiThread(new Runnable()
		    {

			@Override
			public void run()
			{

			    AlertDialog.Builder dialog = new AlertDialog.Builder(UsersFragment.getInstance().getActivity());
			    dialog.setMessage("get users errors: " + errors).create().show();

			}
		    });
		}
	    }
	}

    }

    private String usersListToChatName()
    {

	String chatName = "";
	for (QBUser user : usersAdapter.getSelected())
	{
	    String prefix = chatName.equals("") ? "" : ", ";
	    chatName = chatName + prefix + user.getLogin();
	}
	return chatName;
    }

    public void startSingleChat(QBDialog dialog)
    {

	Bundle bundle = new Bundle();
	bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
	bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

	ChatActivity.start(getActivity(), bundle);
	getActivity().finish();
    }

    private void startGroupChat(QBDialog dialog)
    {

	Bundle bundle = new Bundle();
	bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);
	bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);

	ChatActivity.start(getActivity(), bundle);
	getActivity().finish();
    }

    private void loadNextPage()
    {

	++currentPage;

	QBUsers.getUsers(getQBPagedRequestBuilder(currentPage), UsersFragment.this);
    }

    private void createDialogGroupName(Context context)
    {

	// custom dialog
	final Dialog dialog = new Dialog(context);
	dialog.setContentView(R.layout.dialog_create_group_name);
	dialog.setTitle("Enter Group Name");

	final EditText txtGroupName = (EditText) dialog.findViewById(R.id.txtGroupName);
	Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
	Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

	btnCancel.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		dialog.dismiss();

	    }
	});

	btnOk.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		String groupName = txtGroupName.getText().toString().trim();

		// ApplicationSingleton.addDialogsUsers(usersAdapter.getSelected());

		// Create new group dialog
		QBDialog dialogToCreate = new QBDialog();
		QBUser userLogin = SharePrefsHelper.getCurrentLoginUser(getActivity());
		dialogToCreate.setName(groupName + ": " + userLogin.getLogin() + " :" + usersListToChatName());

		dialogToCreate.setType(QBDialogType.GROUP);

		dialogToCreate.setOccupantsIds(StaticFunction.getUserIds1(usersAdapter.getSelected()));

		QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
		{

		    @Override
		    public void onSuccess(QBDialog dialog, Bundle args)
		    {

			startGroupChat(dialog);

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage("dialog creation errors: " + errors).create().show();
		    }
		});

		dialog.dismiss();
	    }
	});

	dialog.show();
    }

}
