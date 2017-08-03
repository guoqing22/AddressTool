import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Tool extends JFrame {
	private JMenuBar bar;// 定义菜单栏
	private JMenu fileMenu, toolMenu;
	private JMenuItem openItem, autoItem, saveItem, tongJiItem, closeItem, tongJiItemB;// 定义菜单项
	private FileDialog openDia, saveDia;// 定义“打开、保存”对话框
	private JTextArea textMain;// 原始数据文本域
	private static JTextArea textAuto;// 自动生成文本域
	private JTextArea textAlter;// 修改文本域
	private JTextArea textDictionary;// 字典文本域
	private String dirpath;// 获取打开文件路径并保存到字符串中。
	private String fileName;// 获取打开文件名称并保存到字符串中
	private JButton lastB = new JButton("上一条");// 翻页按钮
	private JButton nextB = new JButton("下一条");// 翻页按钮
	private JTextField pageT = new JTextField();// 翻页页数显示
	private JButton copyB = new JButton("=>");// 自动修改复制到手动修改文本框按钮
	private JButton alterB = new JButton("确认");// 修改确认按钮
	private JButton dicB = new JButton("添加");// 添加词典按钮
	private JRadioButton fsyB = new JRadioButton("方式一");// 方式按钮
	private List<String> text = new ArrayList<>();
	private int index = 0;
	private int i = 0;
	private File file;
	private static HashSet<String> DIC = new HashSet<>();
	private static HashSet<String> DIC_SHENG = new HashSet<>();
	private static HashSet<String> DIC_SHI = new HashSet<>();
	private static HashSet<String> DIC_XIAN = new HashSet<>();
	private static HashSet<String> DIC_XIANG = new HashSet<>();
	private static HashSet<String> DIC_CUN = new HashSet<>();
	private static List<Map<Integer, Info>> list = new ArrayList<Map<Integer, Info>>();
	private static Map<Integer, Info> map = new HashMap<Integer, Info>();
	private static Hashtable<String, Integer> ht = new Hashtable<>();
	private static Info info = null;
	private static int MAX_LENGTH;

	Tool() {
		init();
	}

	public void init() {
		// 菜单栏
		bar = new JMenuBar();// 创建菜单栏

		fileMenu = new JMenu("文件(F)");// 创建“文件”菜单
		fileMenu.setMnemonic('f'); // 助记符
		openItem = new JMenuItem("打开(O)");// 创建“打开"菜单项
		openItem.setMnemonic('f'); // 助记符
		saveItem = new JMenuItem("保存(S)");// 创建“保存"菜单项
		saveItem.setMnemonic('s'); // 助记符
		closeItem = new JMenuItem("退出(X)");// 创建“退出"菜单项
		closeItem.setMnemonic('x'); // 助记符
		bar.add(fileMenu);// 将文件添加到菜单栏上

		fileMenu.add(openItem);// 将“打开”菜单项添加到“文件”菜单上
		fileMenu.add(saveItem);// 将“保存”菜单项添加到“文件”菜单上
		fileMenu.add(closeItem);// 将“退出”菜单项添加到“文件”菜单上

		toolMenu = new JMenu("工具(T)");// 创建“工具”菜单
		toolMenu.setMnemonic('t'); // 助记符
		autoItem = new JMenuItem("生成(B)");// 创建“生成"菜单项
		autoItem.setMnemonic('b'); // 助记符
		tongJiItem = new JMenuItem("统计(C)");// 创建“统计"菜单项
		tongJiItem.setMnemonic('c');
		tongJiItemB = new JMenuItem("统计并保存(A)");// 创建“统计并保存"菜单项
		tongJiItemB.setMnemonic('a');

		toolMenu.add(autoItem);// 将“生成”菜单项添加到“文件”菜单上
		toolMenu.add(tongJiItem);// 将“统计”菜单项添加到“文件”菜单上
		toolMenu.add(tongJiItemB);// 将“统计并保存”菜单项添加到“文件”菜单上

		bar.add(fileMenu);// 将文件添加到菜单栏上
		bar.add(toolMenu);// 将文件添加到菜单栏上

		this.setJMenuBar(bar);// 将此窗体的菜单栏设置为指定的菜单栏。
		openDia = new FileDialog(this, "打开", FileDialog.LOAD);
		saveDia = new FileDialog(this, "保存", FileDialog.SAVE);

		// 原始数据文本框
		textMain = new JTextArea(10, 1);
		textMain.setAutoscrolls(true);
		textMain.setEditable(false);
		textMain.setWrapStyleWord(false);
		textMain.setLineWrap(true); // 激活自动换行功能
		textMain.setWrapStyleWord(true);
		// JScrollPane mainScroll = new JScrollPane(textMain);
		textMain.setBorder(BorderFactory.createBevelBorder(1));
		JPanel upPanel = new JPanel(new BorderLayout(15, 15));
		upPanel.add(textMain, BorderLayout.NORTH);

		// 原始数据翻页按钮
		JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

		pageT.setText("1");
		pageT.setColumns(5);
		pageT.setHorizontalAlignment(JTextField.CENTER);
		// pageT.setEditable(false);
		pagePanel.add(lastB);
		pagePanel.add(pageT);

		pagePanel.add(nextB);

		upPanel.add(pagePanel, BorderLayout.CENTER);
		// 自动生成数据区域
		JPanel autoPanel = new JPanel(new BorderLayout()); // 主
		JPanel autoTextPanel = new JPanel(); // 中 文本框
		autoTextPanel.setBorder(BorderFactory.createTitledBorder("自动地址提取预览"));
		textAuto = new JTextArea(22, 45);
		textAuto.setEditable(false);
		JScrollPane autoScroll = new JScrollPane(textAuto);
		textAuto.setBorder(BorderFactory.createBevelBorder(1));
		autoTextPanel.add(autoScroll);
		autoPanel.add(autoTextPanel, BorderLayout.CENTER);

		JPanel fsyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		fsyPanel.add(fsyB);
		autoPanel.add(fsyPanel, BorderLayout.SOUTH);
		JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

		copyPanel.add(copyB);
		copyB.setPreferredSize(new Dimension(50, 50));
		;
		autoPanel.add(copyPanel, BorderLayout.EAST);
		// 修改区域
		JPanel alterPanel = new JPanel(new BorderLayout()); // 主
		JPanel alterTextPanel = new JPanel(); // 中 文本框
		alterTextPanel.setBorder(BorderFactory.createTitledBorder("手动修改"));
		textAlter = new JTextArea(22, 45);
		JScrollPane alterScroll = new JScrollPane(textAlter);
		textAlter.setBorder(BorderFactory.createBevelBorder(1));
		alterTextPanel.add(alterScroll);
		alterPanel.add(alterTextPanel, BorderLayout.CENTER);

		JPanel alterButtonP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
		alterButtonP.add(alterB);
		alterPanel.add(alterTextPanel, BorderLayout.NORTH);
		alterPanel.add(alterButtonP, BorderLayout.CENTER);

		// 字典
		JPanel dicPanel = new JPanel(new BorderLayout()); // 主
		JPanel dicTextPanel = new JPanel(); // 中 文本框
		dicTextPanel.setBorder(BorderFactory.createTitledBorder("字典"));
		textDictionary = new JTextArea(22, 20);
		JScrollPane dicScroll = new JScrollPane(textDictionary);
		textDictionary.setBorder(BorderFactory.createBevelBorder(1));
		dicTextPanel.add(dicScroll);
		dicPanel.add(dicTextPanel, BorderLayout.CENTER);

		JPanel dicButtonP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
		dicButtonP.add(dicB);
		dicPanel.add(dicTextPanel, BorderLayout.NORTH);
		dicPanel.add(dicButtonP, BorderLayout.CENTER);

		this.setLayout(new BorderLayout(10, 10));
		this.add(upPanel, BorderLayout.NORTH);
		this.add(autoPanel, BorderLayout.WEST);
		this.add(alterPanel, BorderLayout.CENTER);
		this.add(dicPanel, BorderLayout.EAST);
		// this.setSize(400, 300);
		myEvent();
		this.pack();

	}

	private void myEvent() {
		// 打开菜单项监听
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// openItem.setEnabled(false);
				openDia.setVisible(true);// 显示打开文件对话框

				dirpath = openDia.getDirectory();// 获取打开文件路径并保存到字符串中。
				fileName = openDia.getFile();// 获取打开文件名称并保存到字符串中

				if (dirpath == null || fileName == null)// 判断路径和文件是否为空
					return;
				else
					textMain.setText(null);// 文件不为空，清空原来文件内容。
				file = new File(dirpath, fileName);// 创建新的路径和名称

				try {
					text.clear();
					index = 0;
					if (list != null && !list.isEmpty()) {

						for (Map<Integer, Info> m : list) {
							for (Integer k : m.keySet()) {
								m.remove(k);
								// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
							}
						}
					}
					list.clear();
					textAuto.setText("");
					String line = null;// 变量字符串初始化为空
					InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
					BufferedReader br = new BufferedReader(read);
					while ((line = br.readLine()) != null) {
						text.add(index, line);
						index = index + 1;

					}
					JOptionPane.showMessageDialog(null, "加载完成！总共" + index + "条数据", "提示信息",
							JOptionPane.INFORMATION_MESSAGE);
					pageT.setText(1 + "");
					textMain.setText(text.get(0));
					br.close();
					read.close();

				} catch (FileNotFoundException e1) {
					// 抛出文件路径找不到异常
					e1.printStackTrace();
				} catch (IOException e1) {
					// 抛出IO异常
					e1.printStackTrace();
				}

			}

		});
		// 生成菜单监听
		autoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// autoItem.setEnabled(false);

				if (text != null && !text.isEmpty()) {
					textAuto.setText("");
					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							m.remove(k);
							// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
						}
					}
					list.clear();

					for (int j = 0; j < text.size(); j++) {
						seg(j, text.get(j));
					}
					for (Map<Integer, Info> m : list) {

						for (Integer k : m.keySet()) {
							if (k == i) {

								textAuto.append(m.get(k).getA().toString() + '\t' + m.get(k).getB().toString() + '\t'
										+ m.get(k).getC().toString() + '\t' + m.get(k).getD().toString() + '\t'
										+ m.get(k).getE().toString() + '\t' + m.get(k).getF().toString() + '\n');
							} else
								break;
							// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
						}
					}

				} else {
					JOptionPane.showMessageDialog(null, "请先打开文件", "提示信息", JOptionPane.ERROR_MESSAGE);

				}
			}
		});

		// 统计菜单项监听
		tongJiItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (list != null && !list.isEmpty()) {
					ht.clear();

					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							String lString = m.get(k).getA().toString() + m.get(k).getB().toString()
									+ m.get(k).getC().toString() + m.get(k).getD().toString()
									+ m.get(k).getE().toString() + m.get(k).getF().toString();
							if (ht.containsKey(lString)) {
								ht.put(lString, ht.get(lString) + 1);
							} else {
								ht.put(lString, 1);
							}
						}
					}

					List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(ht.entrySet());

					Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
						public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
							return (o2.getValue() - o1.getValue());
							// return
							// (o1.getKey()).toString().compareTo(o2.getKey());
						}
					});
					String inputValue = JOptionPane.showInputDialog("请输入数字");
					while (inputValue.equals("") || Integer.parseInt(inputValue) < 0) {

						JOptionPane.showMessageDialog(null, "请输入数字", "提示信息", JOptionPane.ERROR_MESSAGE);
						inputValue = JOptionPane.showInputDialog("请输入数字");
					}
					String tongJi = "";
					if (Integer.parseInt(inputValue) == 0) {
						for (int i = 0; i < infoIds.size(); i++) {
							String id = infoIds.get(i).toString();
						}
						JOptionPane.showMessageDialog(null, "统计完成！总共" + infoIds.size() + "条数据", "提示信息",
								JOptionPane.INFORMATION_MESSAGE);
					} else if (Integer.parseInt(inputValue) <= infoIds.size() && Integer.parseInt(inputValue) > 0) {

						for (int i = 0; i < Integer.parseInt(inputValue); i++) {
							String id = infoIds.get(i).toString();
							tongJi = tongJi + id + "\n";
						}
						JOptionPane.showMessageDialog(null, tongJi, "统计前" + inputValue + "条数据",
								JOptionPane.INFORMATION_MESSAGE);

					} else if (Integer.parseInt(inputValue) > infoIds.size() && Integer.parseInt(inputValue) < 0) {

						JOptionPane.showMessageDialog(null, "输入范围1~" + infoIds.size(), "提示信息",
								JOptionPane.ERROR_MESSAGE);

					}
				} else {
					JOptionPane.showMessageDialog(null, "请打开文件并生成提取信息", "提示信息", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		// 统计保存菜单项监听
		tongJiItemB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (list != null && !list.isEmpty()) {
					ht.clear();
					ht.clear();

					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							String lString = m.get(k).getA().toString() + m.get(k).getB().toString()
									+ m.get(k).getC().toString() + m.get(k).getD().toString()
									+ m.get(k).getE().toString() + m.get(k).getF().toString();
							if (ht.containsKey(lString)) {
								ht.put(lString, ht.get(lString) + 1);
							} else {
								ht.put(lString, 1);
							}
						}
					}

					List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(ht.entrySet());

					Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
						public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
							return (o2.getValue() - o1.getValue());
							// return
							// (o1.getKey()).toString().compareTo(o2.getKey());
						}
					});
					String inputValue = JOptionPane.showInputDialog("请输入数字");
					while (inputValue.equals("") || Integer.parseInt(inputValue) < 0) {

						JOptionPane.showMessageDialog(null, "请输入数字", "提示信息", JOptionPane.ERROR_MESSAGE);
						inputValue = JOptionPane.showInputDialog("请输入数字");
					}
					// 保存统计
					if (Integer.parseInt(inputValue) == 0) {

						if (file != null) {
							saveDia.setVisible(true);// 显示保存文件对话框
							String dirpath = saveDia.getDirectory();// 获取保存文件路径并保存到字符串中。
							String fileName = saveDia.getFile();//// 获取打保存文件名称并保存到字符串中

							if (dirpath == null || fileName == null)// 判断路径和文件是否为空
								return;// 空操作
							else
								file = new File(dirpath, fileName);// 文件不为空，新建一个路径和名称
						}
						try {
							OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
							BufferedWriter bw = new BufferedWriter(write);
							for (int i = 0; i < infoIds.size(); i++) {
								String id = infoIds.get(i).toString();
								bw.write(id);
								bw.newLine();
							}

							bw.close();// 关闭文件
							write.close();
						} catch (IOException e1) {
							// 抛出IO异常
							e1.printStackTrace();
						}

					} else if (Integer.parseInt(inputValue) > infoIds.size() && Integer.parseInt(inputValue) < 0) {

						JOptionPane.showMessageDialog(null, "输入范围1~" + infoIds.size(), "提示信息",
								JOptionPane.ERROR_MESSAGE);

					} else if (Integer.parseInt(inputValue) <= infoIds.size() && Integer.parseInt(inputValue) > 0) {
						if (file != null) {
							saveDia.setVisible(true);// 显示保存文件对话框
							String dirpath = saveDia.getDirectory();// 获取保存文件路径并保存到字符串中。
							String fileName = saveDia.getFile();//// 获取打保存文件名称并保存到字符串中
							if (dirpath == null || fileName == null)// 判断路径和文件是否为空
								return;// 空操作
							else
								file = new File(dirpath, fileName);// 文件不为空，新建一个路径和名称
						}
						try {
							OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
							BufferedWriter bw = new BufferedWriter(write);
							for (int i = 0; i < Integer.parseInt(inputValue); i++) {
								String id = infoIds.get(i).toString();
								bw.write(id);
								bw.newLine();
							}
							bw.close();// 关闭文件
							write.close();
						} catch (IOException e1) {
							// 抛出IO异常
							e1.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "请打开文件并生成提取信息", "提示信息", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		// 退出菜单项监听
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});

		// 窗体关闭监听
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);

			}

		});
		// 下一条
		nextB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (index == 0) {
					JOptionPane.showMessageDialog(null, "请点击文件,打开。", "提示信息", JOptionPane.ERROR_MESSAGE);
				} else if (i < 0) {
					textMain.setText(null);
					pageT.setText(1 + "");
				} else if (i >= index) {
					JOptionPane.showMessageDialog(null, "已经是最后一条数据！", "提示信息", JOptionPane.ERROR_MESSAGE);
				} else {
					i = i + 1;
					textMain.setText(text.get(i));
					int j = i + 1;
					pageT.setText(j + "");

					textAuto.setText("");
					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							// System.out.println(k);
							if (k == i)
								textAuto.append(m.get(k).getA().toString() + '\t' + m.get(k).getB().toString() + '\t'
										+ m.get(k).getC().toString() + '\t' + m.get(k).getD().toString() + '\t'
										+ m.get(k).getE().toString() + '\t' + m.get(k).getF().toString() + '\n');
							else
								break;
							// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
						}
					}
				}

			}
		});
		// 上一条
		lastB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (index == 0) {
					JOptionPane.showMessageDialog(null, "请点击文件,打开。", "提示信息", JOptionPane.ERROR_MESSAGE);
				} else if (i <= 0) {
					JOptionPane.showMessageDialog(null, "已经是第一条数据！", "提示信息", JOptionPane.ERROR_MESSAGE);
				} else {
					i = i - 1;
					textMain.setText(text.get(i));
					int j = i + 1;
					pageT.setText(j + "");

					textAuto.setText("");
					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							// System.out.println(k);
							if (k == i)
								textAuto.append(m.get(k).getA().toString() + '\t' + m.get(k).getB().toString() + '\t'
										+ m.get(k).getC().toString() + '\t' + m.get(k).getD().toString() + '\t'
										+ m.get(k).getE().toString() + '\t' + m.get(k).getF().toString() + '\n');
							else
								break;
							// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
						}
					}
				}
			}
		});
		// 页码
		pageT.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int p = Integer.parseInt(pageT.getText()) - 1;
				if (p < 0 || p > text.size()) {
					JOptionPane.showMessageDialog(null, "请输入正确的数字", "提示信息", JOptionPane.ERROR_MESSAGE);
				} else {
					i = p;
					textMain.setText("");
					textMain.setText(text.get(p));
					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							// System.out.println(k);
							if (k == p)
								textAuto.append(m.get(k).getA().toString() + '\t' + m.get(k).getB().toString() + '\t'
										+ m.get(k).getC().toString() + '\t' + m.get(k).getD().toString() + '\t'
										+ m.get(k).getE().toString() + '\t' + m.get(k).getF().toString() + '\n');
							else
								break;
							// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
						}
					}
				}
			}
		});
		// 复制
		copyB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				textAlter.setText("");
				textAlter.setText(textAuto.getText());
			}
		});
		// 修改确认
		alterB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// TODO Auto-generated method stub
				List<String> listd = new ArrayList<>();
				for (Map<Integer, Info> m : list) {
					for (Integer k : m.keySet()) {
						// System.out.println(k);
						if (k == i) {
							// System.out.println(i);
							m.remove(k);
						} else
							break;
					}
				}
				// System.out.println(textAlter.getText());
				// 删除
				for (Map<Integer, Info> m : list) {
					for (Integer k : m.keySet()) {
						// System.out.println(k);
						if (k == i)
							m.remove(k);
						else
							break;
					}
				}
				// 添加
				String[] tat = { "", "", "", "", "", "", "", "", "" };
				String[] tan = textAlter.getText().split("\n");
				// System.out.println(tan[0]+"\n"+tan[1]);
				for (int j = 0; j < tan.length; j++) {

					tat = tan[j].split("\t");

					for (int k = 0; k < tat.length; k++) {
						if (!listd.contains(tat[k])) {
							listd.add(tat[k]);
						}

					}
					if (tat.length == 1) {
						info = new Info();
						info.setA(tat[0]);
						info.setB("");
						info.setC("");
						info.setD("");
						info.setE("");
						info.setF("");
						map = new HashMap<Integer, Info>();
						map.put(i, info);
						list.add(map);
					}
					if (tat.length == 2) {
						info = new Info();
						info.setA(tat[0]);
						info.setB(tat[1]);
						info.setC("");
						info.setD("");
						info.setE("");
						info.setF("");
						map = new HashMap<Integer, Info>();
						map.put(i, info);
						list.add(map);
					}
					if (tat.length == 3) {
						info = new Info();
						info.setA(tat[0]);
						info.setB(tat[1]);
						info.setC(tat[2]);
						info.setD("");
						info.setE("");
						info.setF("");
						map = new HashMap<Integer, Info>();
						map.put(i, info);
						list.add(map);
					}
					if (tat.length == 4) {
						info = new Info();
						info.setA(tat[0]);
						info.setB(tat[1]);
						info.setC(tat[2]);
						info.setD(tat[3]);
						info.setE("");
						info.setF("");
						map = new HashMap<Integer, Info>();
						map.put(i, info);
						list.add(map);
					}
					if (tat.length == 5) {
						info = new Info();
						info.setA(tat[0]);
						info.setB(tat[1]);
						info.setC(tat[2]);
						info.setD(tat[3]);
						info.setE(tat[4]);
						info.setF("");
						map = new HashMap<Integer, Info>();
						map.put(i, info);
						list.add(map);
					}
					if (tat.length == 6) {
						info = new Info();
						info.setA(tat[0]);
						info.setB(tat[1]);
						info.setC(tat[2]);
						info.setD(tat[3]);
						info.setE(tat[4]);
						info.setF(tat[5]);
						map = new HashMap<Integer, Info>();
						map.put(i, info);
						list.add(map);
					}
				}
				for (Map<Integer, Info> m : list) {
					for (Integer k : m.keySet()) {
						if (k == i) {
							textAuto.append(m.get(k).getA().toString() + '\t' + m.get(k).getB().toString() + '\t'
									+ m.get(k).getC().toString() + '\t' + m.get(k).getD().toString() + '\t'
									+ m.get(k).getE().toString() + '\t' + m.get(k).getF().toString() + '\n');
						} else
							break;
						// bw.write(m.get(k).getA().toString()+'\t'+m.get(k).getB().toString()+'\t'+m.get(k).getC().toString()+'\n');//将获取文本内容写入到字符输出流
					}
				}
				// 显示字典中没有的词
				for (int m = 0; m < listd.size(); m++) {
					if (!DIC.contains(listd.get(m))) {
						textDictionary.append(listd.get(m).toString() + '\n');
					}
				}

			}
		});
		// 字典添加
		dicB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String[] td = textDictionary.getText().split("\n");
				List<String> listd = new ArrayList<>();
				for (int j = 0; j < td.length; j++) {
					listd.add(td[j]);
					DIC.add(td[j]);
				}
				File fileDic = new File("source/dic");
				try {
					OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(fileDic, true), "utf-8");
					BufferedWriter bw = new BufferedWriter(write);

					for (int m = 0; m < listd.size(); m++) {
						bw.write(listd.get(m));
						// System.out.println(listd.get(m));
						bw.newLine();
					}

					textDictionary.setText("");
					bw.close();// 关闭文件
					write.close();
				} catch (IOException e1) {
					// 抛出IO异常
					e1.printStackTrace();
				}
			}
		});
		// 保存菜单项监听
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file != null) {
					saveDia.setVisible(true);// 显示保存文件对话框
					String dirpath = saveDia.getDirectory();// 获取保存文件路径并保存到字符串中。
					String fileName = saveDia.getFile();//// 获取打保存文件名称并保存到字符串中

					if (dirpath == null || fileName == null)// 判断路径和文件是否为空
						return;// 空操作
					else
						file = new File(dirpath, fileName);// 文件不为空，新建一个路径和名称
				}
				try {
					OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
					BufferedWriter bw = new BufferedWriter(write);
					for (Map<Integer, Info> m : list) {
						for (Integer k : m.keySet()) {
							bw.write(m.get(k).getA().toString() + '\t' + m.get(k).getB().toString() + '\t'
									+ m.get(k).getC().toString() + '\t' + m.get(k).getD().toString() + '\t'
									+ m.get(k).getE().toString() + '\t' + m.get(k).getF().toString() + '\n');
						}

					}

					bw.close();// 关闭文件
					write.close();
				} catch (IOException e1) {
					// 抛出IO异常
					e1.printStackTrace();
				}
			}

		});
	}

	/**
	 * 提取方法 a 省 b 市 c 县 d 乡 e 村
	 */
	public static void seg(Integer index, String text) {

		String[] s = text.split("\t");
		for (int l = 0; l < s.length; l++) {
			// System.out.println(s[l]);
		}
		text = text.replaceAll("影响客户[0-9]+个", "");
		text = text.replaceAll("共影响[0-9]+个.*?$", "");
		text = text.replaceAll("影响[0-9]+个.*?$", "");
		text = text.replaceAll("【以上.*?】|【发布.*?】|共影响.*?$|影响户数.*?$|停电用户.*?$|此信息.*?$|（此信息.*?$|此条信息.*?$|本停电信息.*?$"
				+ "|1月.*?|2月.*?|3月.*?|4月.*?|5月.*?|6月.*?|7月.*?|8月.*?|9月.*?|10月.*?|11月.*?|12月.*?"
				+ "|发布渠道：.*?$|共计.*?$|公告渠道.*?$|（重要用户电话.*?$|以上.*?$|（以上.*?$|（河沥供电所已.*?$|该停电信息.*?$"
				+ "|【影响街道/乡镇】|【高危重要客户】|【影响客户】|包括：|周边用户|等|城区：|村庄：|单位：|社区：|位于：|大致范围："
				+ "|主要影响客户范围：|影响的用户：|所辖的村庄：|主要用电户：|大致影响范围："
				+ "|用电村|附近|部分|沿路部分用户|专变|公变|沿路部分客户|全部居民用户停电|及所有屯的用户全部停电|所有屯的用户全部停电"
				+ "|所有的用户全部停电|部分客户|专变|公变|部分用户受影响|一带|（重要）|（短时停电）|停电|影响街道/乡镇|部分用户|主要受影响的范围|影响客户|影响"
				+ "|大客户|用电客户|等区域|等地区用户|等|区域|供电的部分用户|地区用户|高危重要客户|主要包括|主要|重要用户：无|用户|客户|", "");
		String a = "", b = "", c = "", d = "", e = "", f = "";
		String a1 = "", b1 = "", c1 = "", d1 = "", e1 = "", f1 = "";
		String[] aString = text.split("、|，|\\.|；|-| |：|:|;|,|以及|及");
		for (int i = 0; i < aString.length; i++) {

			String aString2 = aString[i].toString();

			aString2 = aString2.replaceAll("[【】。 \" ]", "");

			while (aString2.length() > 0) {
				int len = MAX_LENGTH;
				if (aString2.length() < len) {
					len = aString2.length();
				}
				a1 = a;
				b1 = b;
				c1 = c;
				f1 = f;
				// 取指定的最大长度的文本去词典里面匹配
				String tryWord = aString2.toString().substring(0, 0 + len);
				while (!DIC.contains(tryWord)) {

					// 如果长度为一且在词典中未找到匹配，则按长度为一f="";切分
					if (tryWord.length() == 1) {
						tryWord = aString2;

						break;
					}
					// 如果匹配不到，则长度减一继续匹配
					// System.out.println(tryWord);
					tryWord = tryWord.substring(0, tryWord.length() - 1);

					if ((tryWord.equals(a) && !a.equals("")) || (tryWord.equals(b) && !b.equals(""))
							|| (tryWord.equals(c) && !c.equals("")) || (tryWord.equals(d) && !d.equals(""))
							|| (tryWord.equals(e) && !e.equals(""))) {

						tryWord = aString2;
						break;
					}
					if (!f.equals("")) {
						if (e1.equals("") && DIC_CUN.contains(tryWord)) {
							tryWord = aString2;

							break;
						}
						if (d1.equals("") && DIC_XIANG.contains(tryWord)) {
							tryWord = aString2;

							break;
						}
						if (c.equals("") && DIC_XIAN.contains(tryWord)) {
							tryWord = aString2;

							break;
						}
						if (b.equals("") && DIC_SHI.contains(tryWord)) {
							tryWord = aString2;

							break;
						}
						if (a.equals("") && DIC_SHENG.contains(tryWord)) {
							tryWord = aString2;

							break;
						}

					}
				}

				if (DIC_SHENG.contains(tryWord)) {
					a = tryWord;
					// System.out.println(a);
				} else if (DIC_SHI.contains(tryWord)) {
					b = tryWord;
					// System.out.println(b);
				} else if (DIC_XIAN.contains(tryWord)) {
					c = tryWord;
					// System.out.println(c);
				} else if (DIC_XIANG.contains(tryWord)) {
					// if(text.indexOf(text.indexOf(tryWord)+tryWord.length()).equals(","));
					int indexWordX = text.indexOf(tryWord) + tryWord.length();

					if (indexWordX == text.length()) {
						f = tryWord;
					} else {
						char ch = text.charAt(indexWordX);
						if (ch == '、' || ch == '，' || ch == ',' || ch == ';' || ch == '。')
							f = tryWord;
						else
							d = tryWord;
					}

				}
				// 村
				else if (DIC_CUN.contains(tryWord)) {

					// if(text.indexOf(text.indexOf(tryWord)+tryWord.length()).equals(","));
					int indexWord = text.indexOf(tryWord) + tryWord.length();

					if (indexWord == text.length()) {
						f = tryWord;
					} else {
						char ch = text.charAt(indexWord);
						if (ch == '、' || ch == '，' || ch == ',' || ch == ' ' || ch == ';' || ch == '；' || ch == '。'
								|| ch == '村' || ch == '自')
							f = tryWord;
						else if (ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7'
								|| ch == '8' || ch == '9') {
							tryWord = aString2;
							f = tryWord;
						} else
							e = tryWord;
					}

				}
				// 其他
				else {
					int indexWordl = text.indexOf(tryWord);
					if (indexWordl > 1) {

						// char chl = text.charAt(indexWordl-1);
						/*
						 * if(chl==';'||chl=='；'|chl=='。'||chl=='，'||chl==',')
						 * f="";
						 */
					}
					int indexWordF = text.indexOf(tryWord) + tryWord.length();
					// 结尾
					/*
					 * if(){
					 * 
					 * } else
					 */
					if (indexWordF == text.length()) {
						f = tryWord;
					} else {
						if (indexWordF < 2) {

						} else {
							char ch = text.charAt(indexWordF);
							char chl = text.charAt(indexWordF - 1);
							char chll = text.charAt(indexWordF - 2);

							// System.out.println(chl+" "+ch);
							if (ch == ':' || ch == '：') {
								if (chl == '乡' || chl == '镇' || (chll == '街' && chl == '道'))
									d = tryWord;
								// else if(ch=='村'||(chll=='社'&&chl=='区'))
								else
									e = tryWord;

							} else if (ch == '（') {
								// tryWord=tryWord.substring(beginIndex)
								if (chl == '村' || (chll == '街' && chl == '道') || chl == '乡' || chl == '镇'
										|| (chll == '社') && chll == '区') {

									e = tryWord;

									// aString2=aString2.substring(tryWord.length()+1);
								}
							} else if (chl == '）' && ch == '；' && (chll != '变' || chll != '台')) {

								f = tryWord.substring(0, tryWord.length() - 1);

							}

							else {
								for (int n = 0; n < tryWord.length(); n++) {
									ch = tryWord.charAt(n);
									if (n == 0)
										continue;
									else if (ch == '（' && tryWord.charAt(n - 1) == '村') {
										String[] a11 = tryWord.split("（");
										if (a11.length == 2) {
											e = a11[0];
											f = a11[1];
											break;
										} else {
											e = a11[0];
											break;
										}
									} else if (ch == '（' && tryWord.charAt(n - 1) == '路') {
										String[] a11 = tryWord.split("（");

										if (a11.length == 2) {

											e = a11[0];
											f = a11[1];

											break;
										} else {
											f = a11[0];

											break;
										}
									} else if (ch == '（' && tryWord.charAt(n - 1) == '线') {
										String[] a11 = tryWord.split("（");
										if (a11.length == 2) {
											e = a11[0];
											f = a11[1];
											break;
										} else {
											e = a11[0];
											break;
										}
									} else if (ch == '（' && tryWord.charAt(n - 1) == '道') {
										String[] a11 = tryWord.split("（");
										if (a11.length == 2) {
											d = a11[0];
											f = a11[1];
											break;
										} else {
											f = a11[0];
											break;
										}
									} else {
										f = tryWord;
									}

								}

							}
						}
					}

				}

				if (!f.equals(f1) && !f.equals("")) {
					info = new Info();
					if (f.endsWith("）") || f.endsWith(")")) {
						/*
						 * if(f.length()<2){ break; } else
						 * if(!f.substring(0,f.length()-2).endsWith("变")){
						 * f=f.substring(0,f.length()-1); } else{ f=f; }
						 */
						if (f.startsWith("（") || f.startsWith("(")) {
							f = f.substring(1, f.length());
						}
						info.setA(a);
						info.setB(b);
						info.setC(c);
						info.setD(d);
						info.setE(e);
						info.setF(f);
						map = new HashMap<Integer, Info>();
						map.put(index, info);
						list.add(map);
						e = "";
					} else if (f.endsWith("（")) {
						f = f.substring(0, f.length() - 1);

						info.setA(a);
						info.setB(b);
						info.setC(c);
						info.setD(d);
						info.setE(e);
						info.setF(f);
						map = new HashMap<Integer, Info>();
						map.put(index, info);
						list.add(map);
						e1 = e;
						e = "";
					} else {
						if (e.equals("重要")) {
							e1 = e;
							e = "";

						}
						if (f.startsWith("（") || f.startsWith("(")) {
							f = f.substring(1, f.length());

						}

						info.setA(a);
						info.setB(b);
						info.setC(c);
						info.setD(d);
						info.setE(e);
						info.setF(f);
						map = new HashMap<Integer, Info>();
						map.put(index, info);
						list.add(map);
						if (text.indexOf(tryWord) + tryWord.length() == text.length()) {

						}

						else if (text.charAt(text.indexOf(tryWord) + tryWord.length()) == '。'
								|| text.charAt(text.indexOf(tryWord) + tryWord.length()) == '，') {
							e1 = e;
							e = "";
						} else if (text.charAt(text.indexOf(tryWord) + tryWord.length()) == '；') {
							e1 = e;
							e = "";
						}
					}
				}

				// 从待分词文本中去除已经分词的文本
				aString2 = aString2.substring(tryWord.length());

			}

		}

	}

	/**
	 * 加载字典
	 */
	public static void dic() {
		try {
			System.out.println("开始初始化词典");
			int max = 1;
			int count = 0;
			List<String> lines = Files.readAllLines(Paths.get("source/dic"), Charset.forName("utf-8"));
			List<String> sheng = Files.readAllLines(Paths.get("source/dic_sheng"), Charset.forName("utf-8"));
			List<String> shi = Files.readAllLines(Paths.get("source/dic_shi"), Charset.forName("utf-8"));
			List<String> xian = Files.readAllLines(Paths.get("source/dic_xian"), Charset.forName("utf-8"));
			List<String> xiang = Files.readAllLines(Paths.get("source/dic_xiang"), Charset.forName("utf-8"));
			List<String> cun = Files.readAllLines(Paths.get("source/dic_cun"), Charset.forName("utf-8"));

			for (String line : lines) {
				DIC.add(line);
				count++;
				if (line.length() > max) {
					max = line.length();
				}
			}
			for (String line : sheng) {
				DIC_SHENG.add(line);

			}
			for (String line : shi) {
				DIC_SHI.add(line);

			}
			for (String line : xian) {
				DIC_XIAN.add(line);

			}
			for (String line : xiang) {
				DIC_XIANG.add(line);

			}
			for (String line : cun) {
				DIC_CUN.add(line);

			}

			MAX_LENGTH = max;
			System.out.println("完成初始化词典，词数目：" + count);
			System.out.println("最大分词长度：" + MAX_LENGTH);
		} catch (IOException ex) {
			System.err.println("词典装载失败:" + ex.getMessage());
		}

	}

	public static void writeFile(String s) {
		try {
			File file = new File("source/tongji.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file, true), "utf-8");
			BufferedWriter bw = new BufferedWriter(write);
			bw.write(s);
			bw.newLine();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void clearFile(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			BufferedWriter bw = new BufferedWriter(write);
			bw.write("");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		dic();
		Tool frame = new Tool();
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension screen = tool.getScreenSize();
		Image image = tool.createImage("img/main.png");
		frame.setTitle("地址信息提取工具");
		frame.setIconImage(image);
		frame.setLocation(screen.width / 2 - frame.getWidth() / 2, screen.height / 2 - frame.getHeight() / 2);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}

}
