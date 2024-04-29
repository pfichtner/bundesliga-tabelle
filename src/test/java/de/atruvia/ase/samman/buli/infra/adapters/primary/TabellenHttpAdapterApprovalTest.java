package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.spieltagFsRepo;
import static org.approvaltests.JsonApprovals.verifyJson;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.atruvia.ase.samman.buli.domain.ports.primary.DefaultTabellenService;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;

@SpringBootTest
@AutoConfigureMockMvc
class TabellenHttpAdapterApprovalTest {

	@Autowired
	TabellenHttpAdapter sut;

	// TODO MockMvc fails without explicitly setting an ExceptionHandler (which is
	// there when running the application so it seems, that we are testing our
	// "mock" here) :/
	@ControllerAdvice
	static class GlobalExceptionHandler {

		@ExceptionHandler(Exception.class)
		public ResponseEntity<String> handleException(Exception e) {
			return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = standaloneSetup(sut).setControllerAdvice(new GlobalExceptionHandler()).build();
	}

	@MockBean
	TabellenService tabellenService;

	@Test
	void approveWithRunningGames() throws Exception {
		String league = "bl1";
		String season = "2023-games-running";
		sut = new TabellenHttpAdapter(new DefaultTabellenService(spieltagFsRepo()));
		mockMvc = standaloneSetup(sut).setControllerAdvice(new GlobalExceptionHandler()).build();
		String jsonResponse = mockMvc.perform(get("/tabelle/" + league + "/" + season)).andDo(print()).andReturn()
				.getResponse().getContentAsString();
		verifyJson(jsonResponse);
	}

}
