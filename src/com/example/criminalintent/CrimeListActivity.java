package com.example.criminalintent;

//import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks{

	
	protected Fragment creatFragment() {
		// TODO Auto-generated method stub
		return new CrimeListFragment();
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		//return R.layout.activity_twopane;
		return R.layout.activity_masterdetail;
	}

	@Override
	public void onCrimeSelected(Crime crime) {
		// TODO Auto-generated method stub
		if(findViewById(R.id.detailFragmentContainer) == null){
			Intent i = new Intent(this,CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivity(i);
		} else {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = CrimeFragment.newInstance(crime.getId());
			if(oldDetail != null){
				ft.remove(oldDetail);
			}
			
			ft.add(R.id.detailFragmentContainer,newDetail).commit();
		}
	}

	@Override
	public void onCrimeUpdate(Crime crime) {
		// TODO Auto-generated method stub
		FragmentManager fm = getSupportFragmentManager();
		CrimeListFragment listFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
		listFragment.updateUI();
	}
}
