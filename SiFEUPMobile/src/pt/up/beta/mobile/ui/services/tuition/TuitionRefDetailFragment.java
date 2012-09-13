/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.up.beta.mobile.ui.services.tuition;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.RefMB;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TuitionRefDetailFragment extends Fragment {
	public final static String REFERENCE = "ref";
	private RefMB ref;
	private TextView nome;
	private TextView entidade;
	private TextView referencia;
	private TextView valor;
	private TextView dataIni;
	private TextView dataFim;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ref = getArguments().getParcelable(REFERENCE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ref_mb,
				null);
		nome = (TextView) root.findViewById(R.id.tuition_ref_detail_name);
		entidade = ((TextView) root
				.findViewById(R.id.tuition_ref_detail_entity));
		referencia = (TextView) root
				.findViewById(R.id.tuition_ref_detail_reference);
		valor = (TextView) root.findViewById(R.id.tuition_ref_detail_amount);
		dataIni = (TextView) root
				.findViewById(R.id.tuition_ref_detail_date_start);
		dataFim = (TextView) root
				.findViewById(R.id.tuition_ref_detail_date_end);
		String refStr = Long.toString(ref.getRef());
		while (refStr.length() < 9)
			refStr = "0" + refStr;
		nome.setText(ref.getName());
		entidade.setText(Long.toString(ref.getEntity()));
		referencia.setText(refStr.substring(0, 3) + " "
				+ refStr.substring(3, 6) + " " + refStr.substring(6, 9));
		valor.setText(ref.getAmount() + "€");
		dataIni.setText(ref.getStartDate().format3339(true));
		dataFim.setText(ref.getEndDate().format3339(true));
		return root;
	}
}