package io.test.revolut.sandeep.api;

import io.test.revolut.sandeep.domain.TransferRequest;
import io.test.revolut.sandeep.vo.AccountVO;
import io.test.revolut.sandeep.repository.AccountRepository;
import io.test.revolut.sandeep.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.jooby.Err;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.mvc.Body;
import org.jooby.mvc.GET;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/accounts")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AccountController {
    private final TransferService transferService;
    private final AccountRepository accountRepository;

    @GET
    public List<AccountVO> getAll() {
        return accountRepository.getAll().stream()
                .map(AccountVO::fromDomain)
                .sorted(Comparator.comparing(AccountVO::getAccountNumber))
                .collect(toList());
    }

    @Path("/{id}")
    @GET
    public AccountVO getOne(String id) {
        return accountRepository.findByAccountNumber(id)
                .map(AccountVO::fromDomain)
                .orElseThrow(() -> new Err(404));
    }

    @Path("/transfers")
    @POST
    public Result makeTransfer(@Body TransferRequest transferRequest) {
        transferService.makeTransfer(transferRequest.getSender(), transferRequest.getReceiver(), transferRequest.getAmount());
        return Results.ok();
    }

}
