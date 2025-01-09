
package com.vhbchieu.myapp.view;

import com.vhbchieu.myapp.ChatMessage;
import com.vhbchieu.myapp.MessageType;
import static com.vhbchieu.myapp.MessageType.CHAT;
import static com.vhbchieu.myapp.MessageType.JOIN;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class Home extends javax.swing.JFrame {
    
    private String username;
    private DefaultListModel<String> listModel;
    private StompSessionHandler sessionHandler;
    private StompSession stompSession;  //phiên
    private final int MAX_MESSAGE = 20;

    public Home() {
        initComponents();
        listMessage.setVisible(false);
        listModel = new DefaultListModel<>();
        listMessage.setModel(listModel);
        
        //
        sessionHandler = new StompSessionHandlerAdapter() {
            
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                int result = JOptionPane.showConfirmDialog(Home.this, "Kết nối thành công.", "Thông báo", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION){
                    stompSession = session;
                    listMessage.setVisible(true);
                    btSend.setEnabled(true);
                    txtChat.setEnabled(true);
                    
                    //đăng kí để nhận message từ topic
                    session.subscribe("/topic/public", new StompFrameHandler(){
                        
                        @Override
                        public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
                            return ChatMessage.class;
                        }

                        //
                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            if (listModel.getSize() >= MAX_MESSAGE){
                                listModel.remove(0);
                            }
                            
                            //
                            ChatMessage message = (ChatMessage) payload;
                            switch (message.getType()) {
                                case CHAT -> {
                                    String stringMessage = message.getSender() + ": " + message.getContent();
                                    listModel.add(listModel.getSize(), stringMessage);
                                }
                                case JOIN -> {
                                    String stringMessage = "#" + message.getSender() + " đã tham gia đoạn chat";
                                    listModel.add(listModel.getSize(), stringMessage);
                                }
                                default -> {
                                    String stringMessage = "#" + message.getSender() + " đã rời đoạn chat";
                                    listModel.add(listModel.getSize(), stringMessage);
                                }
                            }
                        }
                    
                    });
                    
                    //
                    ChatMessage joinMessage = new ChatMessage();
                    joinMessage.setType(MessageType.JOIN);
                    joinMessage.setSender(username);
                    //
                    session.send("/app/chat.addUser", joinMessage);
                }
            }
        };
    }
    
    private void connectWebSocket(){
        //Khoi tao transport
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        
        //tao SockJS
        SockJsClient sockJsClient = new SockJsClient(transports);
        
        //tao stomp tu sockJS
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        
        //thiet lap convert chuyen java <-> json
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        
        stompClient.connect("ws://localhost:8080/ws", sessionHandler);
        
    }
    
    private void disconnectWebSocket(){
        stompSession.disconnect();
    }
    
    private void sendMessage(String message){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(username);
        chatMessage.setContent(message);
        chatMessage.setType(MessageType.CHAT);
        
        //
        stompSession.send("/app/chat.sendMessage", chatMessage);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHome = new javax.swing.JPanel();
        btStart = new javax.swing.JButton();
        txtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listMessage = new javax.swing.JList<>();
        txtChat = new javax.swing.JTextField();
        btSend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        panelHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btStart.setText("Start");
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });
        panelHome.add(btStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 120, 30));
        panelHome.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 247, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Name");
        panelHome.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 60, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Chat Application");
        panelHome.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 260, 40));

        listMessage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "message", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(51, 204, 255))); // NOI18N
        listMessage.setEnabled(false);
        jScrollPane1.setViewportView(listMessage);

        panelHome.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 350, 240));

        txtChat.setEnabled(false);
        panelHome.add(txtChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 260, 30));

        btSend.setText("Send");
        btSend.setEnabled(false);
        btSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSendActionPerformed(evt);
            }
        });
        panelHome.add(btSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 440, 80, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelHome, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelHome, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        if (btStart.getText().equals("Start")){
            if (txtName.getText().isBlank()){
                JOptionPane.showMessageDialog(Home.this, "Chưa nhập tên người dùng");
            } else {
                this.username = txtName.getText();
                connectWebSocket();
                txtName.setEnabled(false);
                btStart.setText("Disconnect");
            }
        } else {
            disconnectWebSocket();
            btStart.setText("Start");
        }
    }//GEN-LAST:event_btStartActionPerformed

    private void btSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSendActionPerformed
        if (!txtChat.getText().isBlank()){
            sendMessage(txtChat.getText());
        }
    }//GEN-LAST:event_btSendActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSend;
    private javax.swing.JButton btStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listMessage;
    private javax.swing.JPanel panelHome;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
