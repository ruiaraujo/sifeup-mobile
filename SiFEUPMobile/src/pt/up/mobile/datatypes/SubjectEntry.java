package pt.up.mobile.datatypes;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;


public class SubjectEntry  implements Parcelable {
	private final String ucurr_id;
	private final String ucurr_codigo;
	private final String ucurr_sigla;
	private final String ucurr_nome;
	private final String ucurr_name;
	private final String ucurr_tipo;
	private final String ucurr_duracao_tipo;
	private final String id;
	private final String ocorr_id;
	private final String inst_id;
	private final String comp_id;
	private final String a_lectivo;
	private final int ano;
	private final String per_id;
	private final String per_codigo;
	private final String per_nome;
	private final String tipo;
	private final String estado;
	private final String frequencia;
	private final String avaliacao;
	private final String resultado_melhor;
	private final String resultado_ects;
	private final String aval_a_lectivo;
	private final String resultado_insc;
	private final String creditos_ucn;
	private final String creditos_ects;
	private final String n_ucurrs;
	private final String tipo_ucurrs;
	private final String obr_opt;
	private final String regra_eq;
	private final String devedor;
	private final String incumpridor;
	
	public String getUcurrid() {
		return ucurr_id;
	}
	public String getUcurrcodigo() {
		return ucurr_codigo;
	}
	public String getUcurrsigla() {
		return ucurr_sigla;
	}
	public String getUcurrnome() {
		return ucurr_nome;
	}
	public String getUcurrname() {
		return ucurr_name;
	}
	public String getUcurrtipo() {
		return ucurr_tipo;
	}
	public String getUcurrduracaotipo() {
		return ucurr_duracao_tipo;
	}
	public String getId() {
		return id;
	}
	public String getOcorrid() {
		return ocorr_id;
	}
	public String getInstid() {
		return inst_id;
	}
	public String getCompid() {
		return comp_id;
	}
	public String getAlectivo() {
		return a_lectivo;
	}
	public int getAno() {
		return ano;
	}
	public String getPerid() {
		return per_id;
	}
	public String getPercodigo() {
		return per_codigo;
	}
	public String getPernome() {
		return per_nome;
	}
	public String getTipo() {
		return tipo;
	}
	public String getEstado() {
		return estado;
	}
	public String getFrequencia() {
		return frequencia;
	}
	public String getAvaliacao() {
		return avaliacao;
	}
	public String getResultadomelhor() {
		return resultado_melhor;
	}
	public String getResultadoects() {
		return resultado_ects;
	}
	public String getAvalAnolectivo() {
		return aval_a_lectivo;
	}
	public String getResultadoinsc() {
		return resultado_insc;
	}
	public String getCreditosucn() {
		return creditos_ucn;
	}
	public String getCreditosects() {
		return creditos_ects;
	}
	public String getNucurrs() {
		return n_ucurrs;
	}
	public String getTipoucurrs() {
		return tipo_ucurrs;
	}
	public String getObropt() {
		return obr_opt;
	}
	public String getRegraeq() {
		return regra_eq;
	}
	public String getDevedor() {
		return devedor;
	}
	public String getIncumpridor() {
		return incumpridor;
	}
	@Override
	public int describeContents() {
		return 0;
	}	
	
	
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, ucurr_id);
		ParcelUtils.writeString(dest, ucurr_codigo);
		ParcelUtils.writeString(dest, ucurr_sigla);
		ParcelUtils.writeString(dest, ucurr_nome);
		ParcelUtils.writeString(dest, ucurr_name);
		ParcelUtils.writeString(dest, ucurr_tipo);
		ParcelUtils.writeString(dest, ucurr_duracao_tipo);
		ParcelUtils.writeString(dest, id);
		ParcelUtils.writeString(dest, ocorr_id);
		ParcelUtils.writeString(dest, inst_id);
		ParcelUtils.writeString(dest, comp_id);
		ParcelUtils.writeString(dest, a_lectivo);
		dest.writeInt(ano);
		ParcelUtils.writeString(dest, per_id);
		ParcelUtils.writeString(dest, per_codigo);
		ParcelUtils.writeString(dest, per_nome);
		ParcelUtils.writeString(dest, tipo);
		ParcelUtils.writeString(dest, estado);
		ParcelUtils.writeString(dest, frequencia);
		ParcelUtils.writeString(dest, avaliacao);
		ParcelUtils.writeString(dest, resultado_melhor);
		ParcelUtils.writeString(dest, resultado_ects);
		ParcelUtils.writeString(dest, aval_a_lectivo);
		ParcelUtils.writeString(dest, resultado_insc);
		ParcelUtils.writeString(dest, creditos_ucn);
		ParcelUtils.writeString(dest, creditos_ects);
		ParcelUtils.writeString(dest, n_ucurrs);
		ParcelUtils.writeString(dest, tipo_ucurrs);
		ParcelUtils.writeString(dest, obr_opt);
		ParcelUtils.writeString(dest, regra_eq);
		ParcelUtils.writeString(dest, devedor);
		ParcelUtils.writeString(dest, incumpridor);
	}

	private SubjectEntry(Parcel in) {
		ucurr_id = ParcelUtils.readString(in);
		ucurr_codigo = ParcelUtils.readString(in);
		ucurr_sigla = ParcelUtils.readString(in);
		ucurr_nome = ParcelUtils.readString(in);
		ucurr_name = ParcelUtils.readString(in);
		ucurr_tipo = ParcelUtils.readString(in);
		ucurr_duracao_tipo = ParcelUtils.readString(in);
		id = ParcelUtils.readString(in);
		ocorr_id = ParcelUtils.readString(in);
		inst_id = ParcelUtils.readString(in);
		comp_id = ParcelUtils.readString(in);
		a_lectivo = ParcelUtils.readString(in);
		ano = in.readInt();
		per_id = ParcelUtils.readString(in);
		per_codigo = ParcelUtils.readString(in);
		per_nome = ParcelUtils.readString(in);
		tipo = ParcelUtils.readString(in);
		estado = ParcelUtils.readString(in);
		frequencia = ParcelUtils.readString(in);
		avaliacao = ParcelUtils.readString(in);
		resultado_melhor = ParcelUtils.readString(in);
		resultado_ects = ParcelUtils.readString(in);
		aval_a_lectivo = ParcelUtils.readString(in);
		resultado_insc = ParcelUtils.readString(in);
		creditos_ucn = ParcelUtils.readString(in);
		creditos_ects = ParcelUtils.readString(in);
		n_ucurrs = ParcelUtils.readString(in);
		tipo_ucurrs = ParcelUtils.readString(in);
		obr_opt = ParcelUtils.readString(in);
		regra_eq = ParcelUtils.readString(in);
		devedor = ParcelUtils.readString(in);
		incumpridor = ParcelUtils.readString(in);
	}

	public static final Parcelable.Creator<SubjectEntry> CREATOR = new Parcelable.Creator<SubjectEntry>() {
		public SubjectEntry createFromParcel(Parcel in) {
			return new SubjectEntry(in);
		}

		public SubjectEntry[] newArray(int size) {
			return new SubjectEntry[size];
		}
	};
	
}
