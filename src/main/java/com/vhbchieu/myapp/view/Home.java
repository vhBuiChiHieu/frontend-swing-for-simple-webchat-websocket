
package com.vhbchieu.myapp.view;

import com.vhbchieu.myapp.model.ChatMessage;
import com.vhbchieu.myapp.model.MessageType;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.StringUtils;
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
    private long lastMessageTime;

    public Home() {
        initComponents();
        myInit();
        lastMessageTime = System.currentTimeMillis();
        listMessage.setVisible(false);
        listModel = new DefaultListModel<>();
        listMessage.setModel(listModel);
        
        //
        sessionHandler = new StompSessionHandlerAdapter() {
           
            @Override
            public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {
                int result = JOptionPane.showConfirmDialog(Home.this, "Kết nối thành công.", "Thông báo", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION){
                    listMessage.setVisible(true);
                    btSend.setEnabled(true);
                    txtChat.setEnabled(true);
                    stompSession = session;
                    txtName.setEnabled(false);
                    btStart.setText("Disconnect");
                    
                    //đăng kí để nhận message từ topic
                    session.subscribe("/topic/public", new StompFrameHandler(){
                        
                        @Override
                        public java.lang.reflect.Type getPayloadType(@NonNull StompHeaders headers) {
                            return ChatMessage.class;
                        }

                        //
                        @Override
                        public void handleFrame(@NonNull StompHeaders headers, Object payload) {
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
    
    private void myInit(){
        setLocationRelativeTo(null);
        listMessage.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel itemLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String itemString = value.toString();
                if (itemString.contains(":")){
                   String[] itemSplited = StringUtils.split(itemString, ":");
                   if (itemSplited[0].equals(username)){
                       itemLabel.setText(itemSplited[1]);
                       itemLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                   }
                   else
                       itemLabel.setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    itemLabel.setHorizontalAlignment(SwingConstants.CENTER);
                }
                return itemLabel;
            }
        });
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

            stompClient
                    .connect("ws://localhost:5000/ws", sessionHandler)
                    .addCallback(
                            ss -> {
                                //do something
                    },
                            thr -> JOptionPane.showMessageDialog(this, "Kết Nối Thất Bại!"));
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
    
    private void btSendMessageEvent(){
        if (System.currentTimeMillis() - lastMessageTime >= 2000) {
            if (!txtChat.getText().isBlank()){
                sendMessage(txtChat.getText());
                lastMessageTime = System.currentTimeMillis();
                txtChat.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bạn đang chat quá nhanh");
        }
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
        setTitle("Global chat app");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        btStart.setText("Start");
        btStart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });

        txtName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Username");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Chat Application");

        listMessage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "message", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(51, 204, 255))); // NOI18N
        listMessage.setEnabled(false);
        jScrollPane1.setViewportView(listMessage);

        txtChat.setEnabled(false);
        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtChatKeyReleased(evt);
            }
        });

        btSend.setText("Send");
        btSend.setEnabled(false);
        btSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHomeLayout = new javax.swing.GroupLayout(panelHome);
        panelHome.setLayout(panelHomeLayout);
        panelHomeLayout.setHorizontalGroup(
            panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHomeLayout.createSequentialGroup()
                .addGroup(panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHomeLayout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(btStart, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHomeLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHomeLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHomeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        panelHomeLayout.setVerticalGroup(
            panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHomeLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(20, 20, 20)
                .addComponent(btStart, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(panelHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            }
        } else {
            disconnectWebSocket();
            JOptionPane.showMessageDialog(this, "Đã ngắt kết nối");
            txtName.setEnabled(true);
            btStart.setText("Start");
            txtChat.setText("");
            txtChat.setEnabled(false);
            btSend.setEnabled(false);
            listModel.clear();
            listMessage.setVisible(false);
        }
    }//GEN-LAST:event_btStartActionPerformed

    private void btSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSendActionPerformed
        btSendMessageEvent();
    }//GEN-LAST:event_btSendActionPerformed

    private void txtChatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            btSendMessageEvent();
        }
    }//GEN-LAST:event_txtChatKeyReleased


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
