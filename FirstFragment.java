package com.example.blockchain;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.params.MainNetParams;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;


public class FirstFragment extends Fragment {

    public ArrayList<block> Blockchain = new ArrayList<block>();
    public  TextView edit_t;
    public  TextView TV;
    public Button btn_privatekey;
    String  privkey ;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment




        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edit_t =  view.findViewById(R.id.Edit_transaccion);



        final TextView edit_cuenta =  view.findViewById(R.id.Edit_nCuenta);
        final TextView edit_entrada =  view.findViewById(R.id.Edit_nEntrada);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(FirstFragment.this)
                       // .navigate(R.id.action_FirstFragment_to_SecondFragment);
             //   añadirBloque( edit_t.getText().toString());

                try {
                    String nc = ""+edit_cuenta.getText();
                    String ne = ""+edit_entrada.getText();
                    int child = Integer.parseInt(ne);



                    privkey = CrearWallet(nc, child);
                        AñadirLInea(privkey);

                } catch (MnemonicException.MnemonicLengthException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_privatekey = view.findViewById(R.id.button_privkey);

        view.findViewById(R.id.button_privkey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                ClipboardManager myClipboard = (ClipboardManager) context.getSystemService( CLIPBOARD_SERVICE);
                ClipData myClip;

                String text = privkey;
                myClip = ClipData.newPlainText("privkey", text);
                myClipboard.setPrimaryClip(myClip);
                Snackbar.make(view, "Priv key copiada a portapapeles", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    public void AñadirLInea(String str) {
        //String s =  "" +  edit_t.getText() + System.lineSeparator() + str;

        edit_t.setText( str);



    }
    public String CrearWallet(String ncuenta, int n) throws MnemonicException.MnemonicLengthException {
        int entropyLen = 8;
        byte[] entropy = new byte[entropyLen];
        SecureRandom random = new SecureRandom();
        random.nextBytes(entropy);

        //List<String> words = MnemonicCode.INSTANCE.toMnemonic(entropy);


        List<String> words =   Arrays.asList( "advance",
                "dizzy",
                "buddy",
                "journey",
                "few",
                "annual",
                "spare",
                "fall",
                "struggle",
                "idle",
                "beach",
                "glance");



        String passphrase = "";
        byte seed[] = MnemonicCode.toSeed(words,passphrase);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed);
        String bip44Path = "44H/60H/";//0H/0H";
        bip44Path= bip44Path +ncuenta + "H/0";





        List<ChildNumber> keyPath = HDUtils.parsePath(bip44Path);
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);

        DeterministicKey walletKey = hierarchy.get(keyPath, false, true);

         String pKey = walletKey.getPrivateKeyAsHex();





        DeterministicKey key = HDKeyDerivation.deriveThisOrNextChildKey(walletKey, n);
          //  String r =""+ hierarchy.getNumChildren(walletKey.getPath());


        BigInteger privkey = key.getPrivKey();
        NetworkParameters params = MainNetParams.get();
        ECKey akey = ECKey.fromPrivate(privkey);
        String address = LegacyAddress.fromKey(params, key).toString();

        String r = "" ;
        r = r + "Pubkey: " +  key.getPublicKeyAsHex()  +   System.lineSeparator();
        r = r + " Privkey: " + key.getPrivateKeyAsHex() +   System.lineSeparator();
        r = key.getPrivateKeyAsHex();

        return r ;

    }

    public void  añadirBloque(String tr) {



        String[] transaccion = {tr};

        int prevHash=0;
        if (Blockchain.size() > 0 ) {
            prevHash= Blockchain.get(Blockchain.size() -1).getBlockHash();

        }


        block bloque = new block(transaccion,prevHash);
        Blockchain.add(bloque);



    }
}