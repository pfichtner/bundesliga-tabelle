package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.springframework.ResponseFromResourcesSupplier.responseFromResources;
import static de.atruvia.ase.samman.buli.springframework.RestTemplateMock.configureMock;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.GlobalResultInfo;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo spieltagFsRepo() {
		return new OpenLigaDbSpieltagRepo(configureMock(new RestTemplate(),
				responseFromResources(OpenLigaDbSpieltagRepoMother::resolveMatchdata)), resultinfoProvider(2));
	}

	public static OpenLigaDbTeamRepo teamFsRepo() {
		return new OpenLigaDbTeamRepo(configureMock(new RestTemplate(),
				responseFromResources(OpenLigaDbSpieltagRepoMother::resolveAvailableteams)));
	}

	public static OpenLigaDbResultinfoRepo resultinfoProvider(int globalResultInfoId) {
		return (__league, __season) -> List.of(resultinfo(globalResultInfoId));
	}

	private static Resultinfo resultinfo(int globalResultInfoId) {
		Resultinfo resultinfo = new Resultinfo();
		resultinfo.orderId = 42;
		resultinfo.globalResultInfo = globalResultInfo(globalResultInfoId);
		return resultinfo;
	}

	private static GlobalResultInfo globalResultInfo(int globalResultInfoId) {
		GlobalResultInfo globalResultInfo = new GlobalResultInfo();
		globalResultInfo.id = globalResultInfoId;
		return globalResultInfo;
	}

	private static String resolveMatchdata(String[] parts) {
		return "getmatchdata/%s/%s.json".formatted(parts[parts.length - 2], parts[parts.length - 1]);
	}

	private static String resolveAvailableteams(String[] parts) {
		return "getavailableteams/%s/%s.json".formatted(parts[parts.length - 2], parts[parts.length - 1]);
	}

}
