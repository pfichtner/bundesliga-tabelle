package de.atruvia.ase.samman.buli.infra.internal;

import static java.util.Comparator.comparing;
import static lombok.AccessLevel.PUBLIC;

import java.util.Comparator;
import java.util.List;

import lombok.ToString;
import lombok.experimental.FieldDefaults;

public interface OpenLigaDbResultinfoRepo {

	@ToString
	@FieldDefaults(level = PUBLIC)
	public static class Resultinfo {

		@ToString
		@FieldDefaults(level = PUBLIC)
		public static class GlobalResultInfo {
			int id;
		}

		public static Resultinfo getEndergebnisType(List<Resultinfo> resultinfos) {
			return last(resultinfos);
		}

		private static <T> T last(List<T> list) {
			return list.get(list.size() - 1);
		}

		public static Comparator<Resultinfo> byGlobalResultId = comparing(r -> r.globalResultInfo.id);

		int id;
		String name;
		int orderId;
		GlobalResultInfo globalResultInfo;
	}

	List<Resultinfo> getResultinfos(String league, String season);

}