import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.Random;

public class HotelManager implements ActionListener {
    private static Connection database;
    private String db_id = "database";
    private String db_pw = "database";

    // GUI 원소들을 정의함.
    private JFrame frame = new JFrame();
    private JPanel rowPanel = new JPanel();
    private JPanel colPanel = new JPanel();
    private JPanel bookPanel = new JPanel();
    private JPanel nowPanel = new JPanel();
    private JPanel joinPanel = new JPanel();

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("파일");
    private JMenuItem fileMenuItem = new JMenuItem("열기");

    private JLabel mainText = new JLabel("호텔 관리체계");
    private JLabel nameLabel = new JLabel("고객명");
    private JLabel checkinLabel = new JLabel("체크인(YYYYMMDD)");
    private JLabel daysLabel = new JLabel("묵을 총 날짜");
    private JLabel roomLabel = new JLabel("객실");
    private JButton bookApply = new JButton("예약 등록 및 변경");
    private JTextField nameField = new JTextField();
    private JTextField checkinField = new JTextField();
    private String[] days = {"1", "2", "3", "4", "5"};
    private JComboBox daysBox = new JComboBox(days);
    private JComboBox roomBox = new JComboBox();
    private JButton bookCancel = new JButton("예약 취소");
    private JTabbedPane joinPane = new JTabbedPane();

    private JButton custJoin = new JButton("회원가입");
    private JButton staffJoin = new JButton("직원등록");

    private JFrame customerJoinFrame = new JFrame();
    private JPanel customerJoinPanel = new JPanel();

    private JLabel customerJoinNameLabel = new JLabel("고객명");
    private JLabel customerJoinSexLabel = new JLabel("성별");
    private JLabel customerJoinAddressLabel = new JLabel("주소");
    private JLabel customerJoinPhoneLabel = new JLabel("연락처");

    private String[] sexs = {"남", "여"};
    private String[] citys = {"인천", "경기", "서울", "충남", "제주", "전북", "경북", "울산", "강원", "부산", "대구"};
    private JTextField customerJoinNameInput = new JTextField();
    private JComboBox customerJoinSexBox = new JComboBox(sexs);
    private JComboBox customerJoinAddressBox = new JComboBox(citys);
    private JTextField customerJoinPhoneInput = new JTextField();

    private JButton customerJoinJoinButton = new JButton("가입신청");
    private JButton customerJoinCancelButton = new JButton("취소");

    //staff
    private JFrame staffJoinFrame = new JFrame();
    private JPanel staffJoinPanel = new JPanel();

    private JLabel staffJoinNameLabel = new JLabel("직원명");
    private JLabel staffJoinSexLabel = new JLabel("성별");
    private JLabel staffJoinAddressLabel = new JLabel("주소");
    private JLabel staffJoinPhoneLabel = new JLabel("연락처");

    private JTextField staffJoinNameInput = new JTextField();
    private JComboBox staffJoinSexBox = new JComboBox(sexs);
    private JComboBox staffJoinAddressBox = new JComboBox(citys);
    private JTextField staffJoinPhoneInput = new JTextField();

    private JButton staffJoinJoinButton = new JButton("가입신청");
    private JButton staffJoinCancelButton = new JButton("취소");

    // 검색용
    private JLabel custSearchLabel = new JLabel("고객명");
    private JTextField custSearchInput = new JTextField();
    private JButton custSearchButton = new JButton("조회");

    private JLabel staffSearchLabel = new JLabel("직원명");
    private JTextField staffSearchInput = new JTextField();
    private JButton staffSearchButton = new JButton("조회");

    private JTextArea custSearchOut = new JTextArea();
    private JTextArea staffSearchOut = new JTextArea();
    private JTextArea roomSearchOut = new JTextArea();

    private Border blackline = BorderFactory.createLineBorder(Color.black);

    private JComboBox roomPageBox = new JComboBox();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private Date date = new Date();
    private String now_date = dateFormat.format(date);
    private SimpleDateFormat tonowdateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private String to_now_date = tonowdateFormat.format(date);


    public HotelManager() {
        //디비에 연결함. 및 등
        connectDB();
        rowPanel.setLayout(new GridLayout(3, 1));
        colPanel.setLayout(new GridLayout(1, 2));

        bookPanel.setLayout(new GridLayout(5, 2));
        bookPanel.setBorder(new TitledBorder("투숙 예약"));
        nowPanel.setLayout(new GridLayout(4, 5));


        nowPanel.setBorder(new TitledBorder("객실 예약 현황　　　　　　　　　　　　　　" + now_date));
        joinPanel.setLayout(new GridLayout(1, 1));
        joinPanel.setBorder(new TitledBorder("등록 및 조회"));

        //menu
        menuBar.add(fileMenu);
        fileMenu.add(fileMenuItem);
        frame.setJMenuBar(menuBar);

        //bookPanel
        bookPanel.add(nameLabel);
        bookPanel.add(nameField);
        bookPanel.add(checkinLabel);
        bookPanel.add(checkinField);
        bookPanel.add(daysLabel);
        bookPanel.add(daysBox);
        bookPanel.add(roomLabel);
        bookPanel.add(roomBox);
        bookPanel.add(bookApply);
        bookPanel.add(bookCancel);
        bookApply.addActionListener(this);

        //nowPanel
        try {
            printReserved();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //콤보박스에 디비 받아서 룸들 더함.
        try {
            addRoomsToBOX(roomBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //joinPanel
        //joinPanel.add(joinPane);
        //test 용으로 각종 엘레멘트 등록해봄.
        JPanel custPage = new JPanel(new GridLayout(1, 2));
        JPanel custInputPage = new JPanel(new GridLayout(2, 2));
        joinPanel.add(joinPane);
        custInputPage.add(custSearchLabel);
        custInputPage.add(custSearchInput);
        custInputPage.add(custSearchButton);
        custSearchButton.addActionListener(this);
        custInputPage.add(custJoin);
        custJoin.addActionListener(this);
        custPage.add(custInputPage);
        custPage.add(custSearchOut);
        custSearchOut.setEditable(false);

        //객실조회
        try {
            addRoomsToBOX(roomPageBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JPanel roomPage = new JPanel(new GridLayout(1, 2));
        JPanel roomBoxPage = new JPanel(new GridLayout(2, 2));
        JLabel roomPageLabel = new JLabel("객실");
        roomBoxPage.add(roomPageLabel);
        roomBoxPage.add(roomPageBox);
        roomPageBox.addActionListener(this);
        roomPage.add(roomBoxPage);
        roomSearchOut.setEditable(false);
        roomPage.add(roomSearchOut);


        JPanel staffPage = new JPanel(new GridLayout(1, 2));
        JPanel staffInputPage = new JPanel(new GridLayout(2, 2));
        staffInputPage.add(staffSearchLabel);
        staffInputPage.add(staffSearchInput);
        staffInputPage.add(staffSearchButton);
        staffSearchButton.addActionListener(this);
        staffInputPage.add(staffJoin);
        staffJoin.addActionListener(this);
        staffPage.add(staffInputPage);
        staffPage.add(staffSearchOut);
        staffSearchOut.setEditable(false);

        joinPane.add("고객",custPage);
        joinPane.add("객실", roomPage);
        joinPane.add("직원",staffPage);




        JPanel mainTextPanel = new JPanel(new GridLayout(2, 1));
        mainText.setFont(new Font("맑은 고딕", 1, 24));
        mainText.setHorizontalAlignment(SwingConstants.CENTER);
        JTextArea help = new JTextArea();
        help.setEditable(false);
        help.setText("설명:\n과제 스펙 이외에 예외사항은 별도 예외처리안함.\n" +
                "텍스트 파일은 유니코드인 걸 사용하시오.(한글전용 인코딩 X)");
        mainTextPanel.add(mainText);
        mainTextPanel.add(help);
        rowPanel.add(mainTextPanel);
        rowPanel.add(colPanel);
        rowPanel.add(joinPanel);
        colPanel.add(bookPanel);
        colPanel.add(nowPanel);

        frame.add(rowPanel);

        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        customerJoinPanel.setLayout(new GridLayout(5, 2));
        customerJoinPanel.add(customerJoinNameLabel);
        customerJoinPanel.add(customerJoinNameInput);
        customerJoinPanel.add(customerJoinSexLabel);
        customerJoinPanel.add(customerJoinSexBox);
        customerJoinPanel.add(customerJoinAddressLabel);
        customerJoinPanel.add(customerJoinAddressBox);
        customerJoinPanel.add(customerJoinPhoneLabel);
        customerJoinPanel.add(customerJoinPhoneInput);
        customerJoinPanel.add(customerJoinJoinButton);
        customerJoinPanel.add(customerJoinCancelButton);
        customerJoinCancelButton.addActionListener(this);
        customerJoinJoinButton.addActionListener(this);

        customerJoinFrame.add(customerJoinPanel);
        customerJoinFrame.setSize(300,400);
        customerJoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //staff
        staffJoinPanel.setLayout(new GridLayout(5, 2));
        staffJoinPanel.add(staffJoinNameLabel);
        staffJoinPanel.add(staffJoinNameInput);
        staffJoinPanel.add(staffJoinSexLabel);
        staffJoinPanel.add(staffJoinSexBox);
        staffJoinPanel.add(staffJoinAddressLabel);
        staffJoinPanel.add(staffJoinAddressBox);
        staffJoinPanel.add(staffJoinPhoneLabel);
        staffJoinPanel.add(staffJoinPhoneInput);
        staffJoinPanel.add(staffJoinJoinButton);
        staffJoinPanel.add(staffJoinCancelButton);
        staffJoinJoinButton.addActionListener(this);
        staffJoinCancelButton.addActionListener(this);
        fileMenuItem.addActionListener(new FileOpen());

        staffJoinFrame.add(staffJoinPanel);
        staffJoinFrame.setSize(300,400);
        staffJoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    class FileOpen implements ActionListener {
        JFileChooser chooser;

        FileOpen() {
            chooser = new JFileChooser();
        }

        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt","txt");
            chooser.setFileFilter(filter);

            int ret = chooser.showOpenDialog(null);
            if(ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "취소함","경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String filePath = chooser.getSelectedFile().getPath();

            try{
                insertTXTinDB(filePath);
            } catch (SQLException se) {
                se.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void insertTXTinDB(String path) throws SQLException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        //고객등록
        int number = Integer.parseInt(line.substring(1, line.length())); // 뭔가 텍스트로 저장하면 앞에 문자열이 있나봄
        for (int i = 0; i < number; i++) {
            line = br.readLine();
            String[] words = line.split("\t");
            String sqlStr = "INSERT INTO customers VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = database.prepareStatement(sqlStr);
            stmt.setString(1, words[0]);
            stmt.setString(2, words[1]);
            stmt.setString(3, words[2]);
            stmt.setString(4, words[3]);
            stmt.executeQuery();
            stmt.close();
        }
        line = br.readLine();
        number = Integer.parseInt(line);
        for (int i = 0; i < number; i++) {
            line = br.readLine();
            String[] words = line.split("\t");
            String sqlStr = "INSERT INTO staff VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = database.prepareStatement(sqlStr);
            stmt.setString(1, words[0]);
            stmt.setString(2, words[1]);
            stmt.setString(3, words[2]);
            stmt.setString(4, words[3]);
            stmt.executeQuery();
            stmt.close();
        }
        line = br.readLine();
        number = Integer.parseInt(line);
        for (int i = 0; i < number; i++) {
            line = br.readLine();
            String[] words = line.split("\t");
            String sqlStr = "INSERT INTO rooms VALUES(?, ?, ?)";
            PreparedStatement stmt = database.prepareStatement(sqlStr);
            stmt.setString(1, words[0]);
            stmt.setString(2, words[1]);
            stmt.setString(3, words[2]);
            stmt.executeQuery();
            stmt.close();
        }
        br.close();
        printReserved();
        try {
            addRoomsToBOX(roomBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            addRoomsToBOX(roomPageBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void customerJoin() {
        customerJoinFrame.setVisible(true);
    }

    public void staffJoin() {
        staffJoinFrame.setVisible(true);
    }

    public void custJoinInsert() throws SQLException{
        //custJoin판넬에서 받은 결과를 DB에 저장한다.
        String name = customerJoinNameInput.getText();
        String sex = (String)customerJoinSexBox.getSelectedItem();
        String address = (String)customerJoinAddressBox.getSelectedItem();
        String phone = customerJoinPhoneInput.getText();

        String sqlStr = "INSERT INTO customers VALUES ('" + name + "','" +
                sex + "','" + address + "'," + phone + ")";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.executeQuery();
        stmt.close();

        //성공적으로 저장이 되면
        System.out.print("성공적으로 저장됨.");
        customerJoinFrame.setVisible(false);
    }

    public void staffJoinInsert() throws SQLException{
        //staffjoin 판넬에서 받은 결과를 DB에 저장한다.
        String name = staffJoinNameInput.getText();
        String sex = (String)staffJoinSexBox.getSelectedItem();
        String address = (String)staffJoinAddressBox.getSelectedItem();
        String phone = staffJoinPhoneInput.getText();

        String sqlStr = "INSERT INTO staff VALUES ('" + name + "','" +
                sex + "','" + address + "'," + phone + ")";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.executeQuery();
        stmt.close();

        //성공적으로 저장이 되면
        System.out.print("성공적으로 저장됨.");
        staffJoinFrame.setVisible(false);
    }

    public void bookInsert() throws SQLException{
        //예약 결과를 등록한다.
        String cust_name = nameField.getText();
        String checkIn = checkinField.getText();
        String days = (String)daysBox.getSelectedItem();
        String room_number = (String)roomBox.getSelectedItem();
        String staff_name;

        //이미 예약 처리 (고객이)
        String sqlStr = "SELECT * FROM reservation WHERE customer_name ='"+cust_name+"' and day BETWEEN to_date('"+checkIn +"','YYYYMMDD') and to_date('"+ String.valueOf(Integer.parseInt(checkIn) + Integer.parseInt(days) - 1) + "','YYYYMMDD')";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            System.out.print("같은 기간에 이미 예약함");
            return;
        }

        //이미 등록된 방일 때
        sqlStr = "SELECT * FROM reservation WHERE room_number ="+room_number+" and day BETWEEN to_date('"+checkIn +"','YYYYMMDD') and to_date('"+ String.valueOf(Integer.parseInt(checkIn) + Integer.parseInt(days) - 1) + "','YYYYMMDD')";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();

        if(rs.next()) {
            System.out.print("이미 찬 방임");
            return;
        }

        sqlStr = "SELECT name FROM staff";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        rs.next();
        staff_name = rs.getString("name");
        Random random = new Random();

        while(rs.next() && random.nextBoolean()) {
            staff_name = rs.getString("name");
        }
        stmt.close();
        for (int i = 0; i < Integer.parseInt(days); i++) {
            sqlStr = "INSERT INTO reservation VALUES (" + room_number + ",'" +
                    cust_name + "','" + staff_name + "',to_date('" +
                    String.valueOf(Integer.parseInt(checkIn) + i) +"','YYYYMMDD'),to_date('"  +
                    checkIn + "','YYYYMMDD'))";
            stmt = database.prepareStatement(sqlStr);
            stmt.executeQuery();
        }

        stmt.close();

        printReserved();
    }

    public void printReserved() throws SQLException{
        //예약현황을 프린트한다.
        JLabel[] rooms;
        rooms = new JLabel[20];
        nowPanel.removeAll();
        nowPanel.revalidate();

        String insqlStr;
        PreparedStatement instmt;
        ResultSet inrs;

        String sqlStr = "SELECT room_number FROM rooms";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        int i = 0;
        while(rs.next() && i < 20) {
            rooms[i] = new JLabel(rs.getString("room_number"), JLabel.CENTER);

            //만약 예약되었다면
            insqlStr = "SELECT * FROM reservation where day = to_date('"+
                    to_now_date+"','YYYYMMDD') and room_number = "+rs.getString("room_number");
            instmt = database.prepareStatement(insqlStr);
            inrs = instmt.executeQuery();
            if (inrs.next()) {
                rooms[i].setForeground(Color.red);
            }

            rooms[i].setBorder(blackline);
            nowPanel.add(rooms[i]);
            i++;
        }
        stmt.close();
    }

    public void addRoomsToBOX(JComboBox box) throws SQLException{
        String sqlStr = "SELECT room_number FROM rooms";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            box.addItem(rs.getString("room_number"));
        }

        stmt.close();
    }

    public void searchCust() throws SQLException{
        // 고객을 조회한다.
        // 고객 정보 조회
        String name = custSearchInput.getText();
        String sqlStr = "SELECT * FROM customers WHERE name ='"+name+"'";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String sex = rs.getString("sex");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        rs.next();
        stmt.close();
        //투숙기간 카운팅
        sqlStr = "SELECT count(*) count FROM reservation WHERE customer_name ='"+name+"'";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String countDay;
        if (rs.next()) {
            countDay = rs.getString("count");
        } else {
            countDay = "정보가 없습니다.";
        }
        stmt.close();
        //최근 투숙일 받기, 분명 이런식으로 하는 것보다 더 좋은 방법이 있겠지만 시간과 지식이 부족함.
        sqlStr = "SELECT * FROM reservation WHERE customer_name ='"+name+"' order by day desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String lastDay;
        if (rs.next()) {
            lastDay = rs.getString("day");
        } else {
            lastDay = "정보가 없습니다.";
        }
        stmt.close();
        //최다직원 받기
        sqlStr = "SELECT staff_name, count(staff_name) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE customer_name ='"+name+"' group by staff_name order by count(staff_name) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String staffCount;
        String staffname;
        if (rs.next()) {
            staffCount = rs.getString("count(staff_name)");
            staffname = rs.getString("staff_name");
        } else {
            staffCount = "[정보 없음]";
            staffname = "정보가 없습니다.";
        }
        stmt.close();
        custSearchOut.setText("고객명: "+name+"\n성별: "+sex+
                "\n주소: "+address+"\n연락처: "+phone +"\n총 투숙기간: "+
                countDay+"\n최근 투숙일: "+lastDay+"\n객실전담직원(최다): "+staffname+"("+staffCount+"회)");
    }

    public void searchRoom() throws SQLException{
        //방번호로 방을 조회한다.
        String item = roomPageBox.getSelectedItem().toString();

        String sqlStr = "SELECT * FROM rooms WHERE room_number = ?";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String capacity = rs.getString("capacity");
        String type = rs.getString("type");
        rs.next();

        sqlStr = "SELECT * FROM reservation WHERE room_number = ? and day = to_date('"+to_now_date+"','YYYYMMDD')";
        stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        rs = stmt.executeQuery();
        String now_avail;
        if (rs.next()) {
            now_avail = "투숙중";
        } else {
            now_avail = "비어있음";
        }
        //최다 투숙고객 찾기
        sqlStr = "SELECT customer_name, count(customer_name) FROM (" +
                "select DISTINCT room_number, customer_name, check_in from reservation)" +
                "where room_number = ? group by customer_name order by count(customer_name) desc";
        stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        rs = stmt.executeQuery();
        String cust_info;
        if (rs.next()) {
            cust_info = rs.getString("customer_name");
            cust_info = cust_info + "(" + rs.getString("count(customer_name)")+"회)";
        } else {
            cust_info = "숙박한 사람이 없습니다.";
        }
        //최다 담당직원
        sqlStr = "SELECT staff_name, count(staff_name) FROM (" +
                "select DISTINCT room_number, staff_name, check_in from reservation)" +
                "where room_number = ? group by staff_name order by count(staff_name) desc";
        stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        rs = stmt.executeQuery();
        String staff_info;
        if (rs.next()) {
            staff_info = rs.getString("staff_name");
            staff_info = staff_info + "(" + rs.getString("count(staff_name)")+"회)";
        } else {
            staff_info = "담당한 사람이 없습니다.";
        }
        roomSearchOut.setText("방번호: "+item+"\n수용인원: "+capacity+"\n타입: "+type+"\n상태: "+now_avail+
        "\n투숙고객(최다): " + cust_info+"\n객신전담직원(최다): "+staff_info);
        stmt.close();
    }

    public void searchSfaff() throws SQLException{
        // 이름으로 직원을 조회한다.
        // 직원 정보 조회
        String name = staffSearchInput.getText();
        String sqlStr = "SELECT * FROM staff WHERE name ='"+name+"'";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String sex = rs.getString("sex");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        rs.next();
        stmt.close();
        //접대 고객 최다
        sqlStr = "SELECT customer_name, count(customer_name) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE staff_name ='"+name+"' group by customer_name order by count(customer_name) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String cust_name;
        String cust_count;
        if (rs.next()) {
            cust_name = rs.getString("customer_name");
            cust_count = rs.getString("count(customer_name)");
        } else {
            cust_name = "정보가 없습니다";
            cust_count = "[정보없음]";
        }
        stmt.close();
        //최다 객실 받기
        sqlStr = "SELECT room_number, count(room_number) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE staff_name ='"+name+"' group by room_number order by count(room_number) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String room_number;
        String room_count;
        if (rs.next()) {
            room_number = rs.getString("room_number");
            room_count = rs.getString("count(room_number)");
        } else {
            room_number = "정보가 없습니다.";
            room_count = "[정보없음]";
        }

        stmt.close();
        staffSearchOut.setText("직원명: "+name+"\n성별: "+sex+
                "\n주소: "+address+"\n연락처: "+phone +"\n접대고객(최다): "+
                cust_name+"("+cust_count+"회)"+"\n관리객실(최다): "+room_number+"("+room_count+"회)");
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == custJoin) {
            customerJoin();
        } else if(e.getSource() == staffJoin) {
            staffJoin();
        } else if(e.getSource() == customerJoinCancelButton) {
            customerJoinFrame.setVisible(false);
        } else if(e.getSource() == customerJoinJoinButton) {
            try{
                custJoinInsert();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else if(e.getSource() == staffJoinCancelButton) {
            staffJoinFrame.setVisible(false);
        } else if(e.getSource() == staffJoinJoinButton) {
            try{
                staffJoinInsert();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else if(e.getSource() == custSearchButton) {
            try {
                searchCust();
            } catch (SQLException se) {
                custSearchOut.setText("결과 없습니다.");
            }
        } else if(e.getSource() == staffSearchButton) {
            try {
                searchSfaff();
            } catch (SQLException se) {
                staffSearchOut.setText("결과 없습니다.");
            }
        } else if(e.getSource() == bookApply) {
            try {
                bookInsert();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else if(e.getSource() == roomPageBox) {
            try {
                searchRoom();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void connectDB() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            database = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", db_id, db_pw);
            System.out.println("DB, OK!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLExeption:"+e);
        } catch (Exception e) {
            System.out.println("Exception: "+e);
        }
    }

    public static void main(String[] argv) {
        new HotelManager();
    }
}
