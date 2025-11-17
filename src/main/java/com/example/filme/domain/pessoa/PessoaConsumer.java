@Service
public class PessoaConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.pessoa-queue}")
    private String queueUrl;

    public PessoaConsumer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000) // a cada 2 segundos
    public void consumir() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        for (Message msg : messages) {

            try {
                Pessoa Pessoa = objectMapper.readValue(msg.body(), Pessoa.class);
                System.out.println("Pessoa recebido: " + Pessoa.getTitulo());

                // PROCESSA O Pessoa (sua lógica)
                processarPessoa(Pessoa);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // apaga da fila depois de processar
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build());
        }
    }

    private void processarPessoa(Pessoa Pessoa) {
        // Sua regra de negócio aqui
    }
}
