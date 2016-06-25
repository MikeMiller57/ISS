package com.example.glados.iss;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ISSInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ISSInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ISSInfoFragment extends Fragment {

    private ArrayAdapter<String> listAdapter;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ISSInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ISSInfoFragment newInstance(String param1, String param2) {
        ISSInfoFragment fragment = new ISSInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ISSInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<String> dummyData = new ArrayList<>();
        dummyData.add("value 1");
        dummyData.add("value 2");
        dummyData.add("value 3");
        dummyData.add("value 4");
        dummyData.add("value 5");
        dummyData.add("value 6");
        dummyData.add("value 7");
        dummyData.add("value 8");

        listAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item, // The name of the layout ID.
                        R.id.list_item_textview, // The ID of the textview to populate.
                        new ArrayList<String>());
        for(String s : dummyData)
        {
            listAdapter.add(s);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_issinfo, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);

        FetchISSTask task = new FetchISSTask();
        task.execute();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public class FetchISSTask extends AsyncTask<Void, Void, ArrayList<String>>
    {

        private final String LOG_TAG = FetchISSTask.class.getSimpleName();

        private ArrayList<String> getJsonData(String jsonStr) throws JSONException {
            JSONObject jsonObj = new JSONObject(jsonStr);
            ArrayList<String> rs = new ArrayList<>();
            rs.add("Latitude: " + jsonObj.getString("latitude"));
            rs.add("Longitude: " + jsonObj.getString("longitude"));
            rs.add("Altitude: " + jsonObj.getString("altitude"));
            rs.add("Velocity: " + jsonObj.getString("velocity"));
            return rs;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            String jsonStr = null;
            try {
                URL url = new URL("https://api.wheretheiss.at/v1/satellites/25544");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                jsonStr = buffer.toString();
                conn.disconnect();
                return getJsonData(jsonStr);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            listAdapter.clear();
            for(String s : result)
            {
                listAdapter.add(s);
            }
        }
    }

}
