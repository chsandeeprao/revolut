package io.test.revolut.sandeep;

import io.test.revolut.sandeep.api.AccountController;
import io.test.revolut.sandeep.repository.AccountRepository;
import io.test.revolut.sandeep.repository.InMemoryAccountRepository;
import io.test.revolut.sandeep.service.*;
import org.jooby.Jooby;
import org.jooby.apitool.ApiTool;
import org.jooby.json.Jackson;

public class App extends Jooby {

  {
    use(new Jackson());

    /** A module with domain logic */
    use((env, conf, binder) -> {
      binder.bind(AccountRepository.class).to(InMemoryAccountRepository.class);
      binder.bind(AccountTransactionService.class).to(AccountTransactionServiceImpl.class);
      binder.bind(CurrencyRatesProvider.class).to(CurrencyRatesProviderImpl.class);
      binder.bind(TransferService.class).to(TransferServiceImpl.class);
    });

    /** Actual routes */
    use(AccountController.class);

    /** Export API to Swagger and RAML: */
    use(new ApiTool()
        .swagger()
        .raml());

      /** Populate the repository with test accounts! */
      onStart(reg -> TestAccountsPopulation.populateAccounts(reg.require(AccountRepository.class)));
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

}
