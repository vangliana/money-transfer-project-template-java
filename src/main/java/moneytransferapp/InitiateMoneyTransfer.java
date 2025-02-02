package moneytransferapp;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.UUID;

// @@@SNIPSTART money-transfer-project-template-java-workflow-initiator
public class InitiateMoneyTransfer {

    public static void main(String[] args) throws Exception {

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        for (int i = 0; i < 100; i++) {
            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
                    // A WorkflowId prevents this it from having duplicate instances, remove it to duplicate.
                    //.setWorkflowId("money-transfer-workflow")
                    .setWorkflowId(UUID.randomUUID().toString())
                    .build();
            // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
            WorkflowClient client = WorkflowClient.newInstance(service);
            // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
            MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, options);
            String referenceId = UUID.randomUUID().toString();
            String fromAccount = "001-001";
            String toAccount = "002-002";
            double amount = 18.74;
            // Asynchronous execution. This process will exit after making this call.
            WorkflowExecution we = WorkflowClient.start(workflow::transfer, fromAccount, toAccount, referenceId, amount);
            System.out.printf("\nTransfer of $%f from account %s to account %s is processing\n", amount, fromAccount, toAccount);
            System.out.printf("\nWorkflowID: %s RunID: %s", we.getWorkflowId(), we.getRunId());
        }
        System.exit(0);
    }
}
// @@@SNIPEND
