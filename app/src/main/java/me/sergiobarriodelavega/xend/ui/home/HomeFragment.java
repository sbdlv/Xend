package me.sergiobarriodelavega.xend.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import me.sergiobarriodelavega.xend.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView tvHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvHome = view.findViewById(R.id.text_home);

        // Create a connection and login to the example.org XMPP service.

        /*
        Thread thread = new Thread(new Runnable() {
            AbstractXMPPConnection connection;

            @Override
            public void run() {
                try  {
                    AbstractXMPPConnection conn1 = null;
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword("sergio", "usuario")
                            .setHostAddress(InetAddress.getByName("192.168.1.97"))
                            .setXmppDomain("xend")
                            .setPort(5222)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .build();

                    connection = new XMPPTCPConnection(config);
                    connection.connect().login();

                    tvHome.setText("Conexion realizada");
                    Toast.makeText(getContext(), "Conexion realizada", Toast.LENGTH_LONG).show();


                    //Chat
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    chatManager.addIncomingListener(new IncomingChatMessageListener() {
                        @Override
                        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                            tvHome.setText("De " + from + ": " + message.getBody());

                        }
                    });

                } catch (SmackException.EndpointConnectionException | UnknownHostException | XmppStringprepException e) {
                    Toast.makeText(getContext(), "No se pudo realizar la conexion", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });

        getActivity().runOnUiThread(thread);*/
    }
}