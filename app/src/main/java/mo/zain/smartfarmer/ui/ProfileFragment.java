package mo.zain.smartfarmer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.authentication.IntroActivity;


public class ProfileFragment extends Fragment {

    Button button2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        button2 = view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogFun();
            }
        });
        return view;
    }
    private void LogFun()
    {
        FirebaseAuth.getInstance().signOut();
        getActivity().startActivity(new Intent(getContext(), IntroActivity.class));
        getActivity().finish();
    }
}