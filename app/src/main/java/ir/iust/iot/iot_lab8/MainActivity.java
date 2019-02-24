package ir.iust.iot.iot_lab8;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager WD_Manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WD_Manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = WD_Manager.initialize(this, getMainLooper(), null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (WifiP2pManager.WIFI_P2P_STATE_ENABLED != 2) {
            //there is no WifiDirect interface
            Toast.makeText(this, "no p2pwifi", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "p2pwifi enabled", Toast.LENGTH_SHORT).show();
        }


        // intentFilter
        // add necessary intent values to be matched.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        final TextView hello = findViewById(R.id.hello);

        WD_Manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
                hello.setText("onSuccess");
                WD_Manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Toast.makeText(MainActivity.this, "peers available",
                                Toast.LENGTH_SHORT).show();
                        for (WifiP2pDevice tmp: peers.getDeviceList()) {
                            Toast.makeText(MainActivity.this, tmp.deviceName, Toast.LENGTH_SHORT).show();
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = tmp.deviceAddress;
                            WD_Manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(MainActivity.this, "connect success",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Toast.makeText(MainActivity.this, "connect fail",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Discovery Failed : " + reason,
                        Toast.LENGTH_SHORT).show();
                hello.setText("onFailure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
