package com.example.criminalintent;

//import android.app.Fragment;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity{

	
	protected Fragment creatFragment() {
		// TODO Auto-generated method stub
		return new CrimeListFragment();
	}

}
