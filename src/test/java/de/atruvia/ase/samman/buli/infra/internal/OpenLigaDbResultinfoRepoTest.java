package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.endergebnisType;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.GlobalResultInfo;

class OpenLigaDbResultinfoRepoTest {

	@Test
		void testEndergebnisTypeIsHighestGlobalResultInfo() {
			Resultinfo r1 = resultinfo(1);
			Resultinfo r2 = resultinfo(2);
			assertThat(endergebnisType(List.of(r2, r1))).isSameAs(r2);
		}

	static Resultinfo resultinfo(int globalResultInfoId) {
		Resultinfo resultinfo = new Resultinfo();
		resultinfo.globalResultInfo = globalResultInfo(globalResultInfoId);
		return resultinfo;
	}

	static GlobalResultInfo globalResultInfo(int globalResultInfoId) {
		GlobalResultInfo globalResultInfo = new GlobalResultInfo();
		globalResultInfo.id = globalResultInfoId;
		return globalResultInfo;
	}

}
