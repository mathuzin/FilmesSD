@Service
public class UsuarioConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.usuario-queue}")
    private String queueUrl;

    public UsuarioConsumer(SqsClient sqsClient, ObjectMapper objectMapper) {
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
                Usuario Usuario = objectMapper.readValue(msg.body(), Usuario.class);
                System.out.println("Usuario recebido: " + Usuario.getTitulo());

                // PROCESSA O Usuario (sua lógica)
                processarUsuario(Usuario);

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

    private void processarUsuario(Usuario Usuario) {
        // Sua regra de negócio aqui
    }
}
