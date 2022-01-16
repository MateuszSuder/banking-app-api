package banking.application.repository;

import banking.application.model.*;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {

    // Get account with codes only
    @Query(fields = "{'codes' : 1, '_id' : 0 }")
    BankAccount findItemById(String id);

    // Get account with currency only
    @Query(fields = "{'currencies' : 1, '_id' : 0 }")
    BankAccount findCurrencyById(String id);

    @Query(value = "{ _id : ?0, 'savedRecipients.accountNumber': ?1}", fields = "{savedRecipients: 1}")
    Optional<BankAccount> findAccountByIdAndRecipient(String id, String recipientIban);

    @Aggregation(pipeline = {
            "{ $match: { _id : '?0' }}",
            "{ $project: { _id: 0, lastLoan: { $last: '$loans' }}}",
            "{ $project: { lastInstallment: { $last: '$lastLoan.installments' }}}",
            "{ $project: { isActive : { $toBool: '$lastInstallment.amountLeftToPay' }}}"
    })
    Optional<Boolean> checkIfAccountIsActive(String iban);

    @Aggregation(pipeline = {
            "{ $match: { loans: { $exists: true }}}",
            "{ $unwind: { path: '$currencies' }}",
            "{ $match: { 'currencies.currency': 'PLN' }}",
            "{ $project: {" +
                    "  installments: {" +
                    "    $let: {" +
                    "      vars: {" +
                    "        lastLoan: { $last: '$loans' }" +
                    "      }," +
                    "      in: {" +
                    "        $filter: {" +
                    "          input: '$$lastLoan.installments'," +
                    "          as: 'installment'," +
                    "          cond: { $and: [" +
                    "            {$gte: ['$$NOW', '$$installment.paymentDay']}, " +
                    "            {$gt: ['$$installment.amountLeftToPay', 0]}" +
                    "          ]}" +
                    "        }" +
                    "      }" +
                    "    }" +
                    "  }," +
                    "  info: {$let: {" +
                    "    vars: {" +
                    "      lastLoan: { $last: '$loans' }" +
                    "    }," +
                    "    in: {" +
                    "      interest: '$$lastLoan.interest'" +
                    "      autoPay: '$$lastLoan.autoPayment'" +
                    "    }" +
                    "  }}," +
                    "  amount: '$currencies.amount'" +
                    "  loanId: { $add: [{ $size: '$loans' }, -1]}" +
                    "}}",
            "{ $match: { 'info.autoPay': true}}",
            "{ $project: {" +
                    "  toPay: {$round: " +
                    "    [{ $add: " +
                    "      [{$sum: " +
                    "        ['$installments.amountLeftToPay']}, '$info.interest']" +
                    "    }, 2]}," +
                    "  amount: 1," +
                    "  autoPay: '$info.autoPay'," +
                    "  'installments._id': 1," +
                    "  'installments.amountLeftToPay': 1," +
                    "   loanId: 1," +
                    "   interest: '$info.interest'" +
                    "}}",
            "{ $project: {" +
                    "  _id: 1," +
                    "  ableToPay: { $gte: ['$amount', '$toPay'] }," +
                    "  installments: 1" +
                    "  interest: 1" +
                    "  loanId: 1" +
                    "  toPay: 1" +
                    "}}",
            "{ $match: {" +
                    "  toPay: { $gt: 0}" +
                    "}}"
    })
    List<AccountAbleToPay> getIDsOfAccountsWithActiveLoan();

    @Aggregation(pipeline = {
            "{ $match: {" +
                "loans: { $exists: true }" +
            "}}",
            "{ $project: {" +
                "  startedAtDay: { $dayOfMonth: " +
                "    { $last: '$loans.startedAt' }" +
                "  }," +
                "  dayNow: { $dayOfMonth: '$$NOW' }," +
                "  monthDiff: { $subtract: [" +
                "      { $month: { $last: '$loans.startedAt' }}," +
                "      { $month: '$$NOW'}" +
                "    ]}," +
                "  installments: { $last: '$loans.installments' }," +
                "  auto: { $last: '$loans.autoPayment' }," +
                "  interest: { $last: '$loans.interest' }," +
                "  loanId: { $add: [{ $size: '$loans' }, -1]}" +
            "}}",
            "{ $redact: {" +
                "  $cond: [" +
                "    { $and: [" +
                "        {$eq: ['$startedAtDay', '$dayNow']}," +
                "        {$ne: ['$monthDiff', 0]}," +
                "        {$ne: ['$auto', true]}" +
                "      ]" +
                "    }," +
                "    '$$KEEP'," +
                "    '$$PRUNE'" +
                "  ]" +
            "}}v",
            "{ $project: {" +
                "  _id: 1," +
                "  interest: {$round: [{ $multiply: [{$add: [" +
                "    {$sum: '$installments.amountLeftToPay'}," +
                "    '$interest'" +
                "    ]}, 0.05]}, 2]}," +
                "  loanId: 1" +
            "}}"
    })
    List<AccountWithInterest> getAccountsWithInterestToPay();

    @Aggregation(pipeline = {
            "{ $match: { _id : '?0' }}",
            "{ $project: {" +
                "  _id: 0," +
                "  loanId: { $indexOfArray: ['$loans', { $last: '$loans' }]}" +
            "}}"
    })
    Optional<Integer> getLastLoanId(String iban);

    @Aggregation(pipeline = {
            "{ $match: { _id : '?0' }}",
            "{ $unwind: { path: '$currencies' }}",
            "{ $match: { 'currencies.currency': 'PLN' }}",
            "{ $project: {" +
                    "  amount: {" +
                    "    $let: {" +
                    "      vars: {" +
                    "        lastLoan: { $last: '$loans' }" +
                    "      }," +
                    "      in: {" +
                    "        interest: '$$lastLoan.interest'," +
                    "        installments: '$$lastLoan.installments'" +
                    "      }" +
                    "    }" +
                    "  }," +
                    "  balance: '$currencies.amount'," +
                    "  loanId: { $indexOfArray: ['$loans', { $last: '$loans' }]}" +
                    "}}",
            "{ $project: {" +
                    "  _id: 0," +
                    "  interest: '$amount.interest'," +
                    "  installments: '$amount.installments'," +
                    "  balance: 1," +
                    "  loanId: 1" +
                    "} }",
            "{ $project: {" +
                    "  'installments.paymentDay': 0," +
                    "  'installments.amount': 0" +
                    "}}"
    })
    SingleAccountWithToPay getSingleAccountWithToPay(String iban);

    @Aggregation(pipeline = {
            "{$match: {" +
                    "  _id: ?0, " +
                    "  savedRecipients: {$exists: true}" +
                    "}}",
            "{$project: {id: 0," +
                    "   savedRecipients: 1}}",
            "{$unwind: { path: '$savedRecipients'}}",
            "{$project: {" +
                    "  accountNumber: '$savedRecipients.accountNumber'," +
                    "  recipientName: '$savedRecipients.recipientName'" +
                    "}}"
    })
    List<Recipient> getListOfRecipients(String iban);

    @Aggregation(pipeline = {
            "{$match: {" +
                    "  _id: ?0," +
                    "}}",
            "{$project: {" +
                    "  _id: 0," +
                    "  standingOrders: 1" +
                    "}}",
            "{$unwind: {" +
                    "  path: '$standingOrders'" +
                    "}}",
            "{$project: {" +
                    "  _id: '$standingOrders._id'," +
                    "  title: '$standingOrders.title'," +
                    "  to: '$standingOrders.to'," +
                    "  nextPayment: '$standingOrders.nextPayment'," +
                    "  lastPaymentFailed: '$standingOrders.lastPaymentFailed'," +
                    "  value: '$standingOrders.value'" +
                    "}}"
    })
    List<StandingOrder> getListOfStandingOrders(String iban);
}


