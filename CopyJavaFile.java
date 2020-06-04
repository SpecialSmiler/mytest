import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class CopyJavaFile extends JPanel implements ActionListener,FilenameFilter {
    JFileChooser fc;
    JButton openButton,saveButton;
    JTextArea log;//用于记录打开过的文件
    File srcDir;    //原目录
    File tempDir;   //临时目录
    File dstDir;    //目标目录
    int count=0;
    String myLogo ="/***************************************/\n" +
                 "/*Author:  SpecialSmiler  */\n" +
                 "/*Date:    2020/5/31   */\n" +
                 "/***************************************/\n";

    CopyJavaFile(){
        super(new BorderLayout());

        fc=new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        log=new JTextArea(5,20);
        log.setEditable(false);
        log.setMargin(new Insets(5,5,5,5));
        JScrollPane logScrollPane = new JScrollPane(log);//使TextArea可以滚动

        openButton=new JButton("打开");
        openButton.addActionListener(this);

        saveButton=new JButton("拷贝");
        saveButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);

        add(buttonPanel,BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==openButton){
            //用result来接收返回值,记录用户点了确定还是取消
            int result = fc.showOpenDialog(this);

            if(result==JFileChooser.APPROVE_OPTION){
                //srcDir为源目录（如E:\tmp），
                //目标目录则是srcDir下的archive文件夹（如E:\tmp\archive)
                //因为archive为tmp的子目录，所以不能直接把文件复制进去
                //需要tempDir来作为一个临时目录（如E:\CopyDemoTemp）
                //先把整个tmp文件夹拷贝到CopyDemoTem,
                //然后把tmp文件夹改名为archive，再回头复制给E盘下的tmp
                //最后把E盘中临时的CopyDemoTemp文件夹删除
                srcDir=fc.getSelectedFile();
                tempDir =new File("E:\\CopyDemoTemp");
                dstDir=new File(srcDir,"archive");
                log.append("打开的是："+srcDir.getPath()+"\n");
            }
        }
        if(e.getSource()==saveButton){
            try {
                tempDir.mkdir();
                CopyDirectory(srcDir, tempDir,myLogo);
                File tempFile1 = new File(tempDir,srcDir.getName());
                File tempFile2 = new File(tempDir,"archive");
                tempFile1.renameTo(tempFile2);  //对文件夹进行重命名

                //这是第二次拷贝文件，logo需设置成空字符串，否则将出现两个logo
                CopyDirectory(tempFile2,srcDir,"");
                //删除临时的文件夹
                DeleteDirectory(tempDir);

                String msg="共处理了"+count/2+"个文件";    //因为进行了两次复制操作，所以count要除以2
                JOptionPane.showMessageDialog(this,msg,"秀",JOptionPane.PLAIN_MESSAGE);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".java");  //只有当文件的后缀为java时，返回true
    }

    public void CopyDirectory(File src, File dst, String logo) throws IOException{

        File newDir = new File(dst,src.getName());
        newDir.mkdirs();

        File[] subFiles = src.listFiles();

        for(File subFile:subFiles){
            if(subFile.isFile()){
                 BufferedReader br=new BufferedReader(new FileReader(subFile));
                 BufferedWriter bw=new BufferedWriter(new FileWriter(new File(newDir,subFile.getName())));

                 //当文件满足文件名过滤器时（当文件为.java文件时）
                 //就往文件中写入logo
                 if(accept(subFile,subFile.getName())){
                    bw.write(logo);
                }

                 int len;
                 char[] chs=new char[1024];
                 while ((len=br.read(chs))!=-1){
                     bw.write(chs,0,len);
                 }
                 br.close();
                 bw.close();
                 count++;
            }
            else if(subFile.isDirectory()){
                CopyDirectory(subFile,newDir,logo);  //如果是目录则递归
            }
        }
    }

    public void DeleteDirectory(File dir){
        File[] subFiles = dir.listFiles();
        for(File subFile : subFiles){
            if(subFile.isFile()){
                subFile.delete();
            }
            else if(subFile.isDirectory()){
                DeleteDirectory(subFile); //如果是目录则递归
            }
        }
        dir.delete();
    }

    public static void createAndShowMyGUI(){
        JFrame frame = new JFrame("复制java包");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new CopyJavaFile());
        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        createAndShowMyGUI();
    }
}


