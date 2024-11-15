package org.cordova.quasar.corona.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.datami.smi.SdState;
import com.datami.smi.SdStateChangeListener;
import com.datami.smi.SmiResult;
import com.datami.smi.SmiVpnSdk;
import com.datami.smi.internal.MessagingType;

public class MyApplication extends Application implements SdStateChangeListener {
    private static final String TAG = MainActivity.class.getName();
    public static SdState sdState;
    private Toast toast;
    private Context context;
    private int duration;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        duration = Toast.LENGTH_LONG;
        String mySdkKey = "SUBSTITUIR_POR_CHAVE_DATAMI";
        SmiVpnSdk.initSponsoredData(mySdkKey, this, R.drawable.ic_launcher_background, MessagingType.NONE);
        Log.d(TAG, "VPN Inicializada");
        ensureVpnConnected();
    }

    @Override
    public void onChange(SmiResult currentSmiResult) {
        sdState = currentSmiResult.getSdState();
        Log.d(TAG, "Estado da VPN: " + sdState);

        CharSequence text = "";
        if (sdState == SdState.SD_AVAILABLE) {
            text = "Seu acesso a esse site é gratuito.";
        } else if (sdState == SdState.SD_NOT_AVAILABLE) {
            text = "Seu acesso a esse site poderá acarretar cobranças em seu plano de dados.";
            Log.d(TAG, " - Razão: " + currentSmiResult.getSdReason());
            // Reconectar à VPN automaticamente
            ensureVpnConnected();
        } else if (sdState == SdState.WIFI) {
            // Dispositivo conectado via Wi-Fi
            text = "Acesso via Wi-Fi.";
            Log.d(TAG, "Wi-Fi - Razão: " + currentSmiResult.getSdReason());
        }

        showToast(text);
    }

    // Método para garantir que a VPN esteja conectada
    private void ensureVpnConnected() {
        if (sdState != SdState.SD_AVAILABLE) {
            Log.d(TAG, "Tentando reconectar à VPN...");
            try {
                // Tentativa alternativa caso o método reconnect() não exista
                if (SmiVpnSdk.isVpnConnected()) {
                    Log.d(TAG, "VPN já conectada.");
                } else {
                    // Se o SDK tiver outro método para reconectar ou reconfigurar a VPN
                    SmiVpnSdk.reconnect();  // Se o método estiver disponível, ele será chamado
                    Log.d(TAG, "Tentativa de reconexão à VPN.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao tentar reconectar à VPN: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "VPN já está conectada.");
        }
    }

    // Método utilitário para exibir Toasts
    private void showToast(CharSequence text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
