@Service
public class SqsConsumer {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public SqsConsumer(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Scheduled(fixedRate = 2000)
    public void consume() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        for (Message msg : messages) {
            System.out.println("Mensagem recebida: " + msg.body());

            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build());
        }
    }
}
