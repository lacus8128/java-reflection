/**
 *
 */
package account_keeping.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML で書かれた設定を使い、Bean を呼び出すモジュール
 * （ここでの Bean はアプリケーション固有の処理として手作りされる想定）
 */
public class XMLApplicationContext implements ApplicationContext {

    private final String XML_FILE_NAME = "application_context.xml";
    // private final String BEAN_PACKAGE_NAME = "account_keeping.temp.bean";
    private final String INIT_METHOD = "init";

    public XMLApplicationContext() {
        /* XML を読み込む */
    }

    /**
     * インスタンス生成し、XMLの設定を読み込んで値をセットする
     */
    @Override
    public <T> T getBean(Class<T> beanClazz) {
        String simpleName = beanClazz.getSimpleName();
        T beanInstance = null;
        try {
            beanInstance = beanClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.toString();
        }

        /* XMLを読む準備 */
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.toString();
        }
        InputStream is = null;
        File xmlFile = null;
        try {
            xmlFile = new File(XML_FILE_NAME);
            is = new FileInputStream(xmlFile);
        } catch (FileNotFoundException e) {
            e.toString();
        }
        Document document = null;
        try {
            document = docBuilder.parse(is);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        Element elem = document.getElementById(simpleName);  /* 取得されるのは bean要素 */
        if(elem.getTagName().equals("bean")) {
            NodeList childList = elem.getChildNodes();
            for(int i=0 ; i< childList.getLength() ; i++) {
                Node tempItem = childList.item(i);
                // ノードの型が"Element"であれば、 getNodeName は、Element#getTagName と同じになる
                if(tempItem.getNodeType() == Node.ELEMENT_NODE && tempItem.getNodeName().equals("property")) {
                    Element property = (Element) childList.item(i);
                    /* property 要素の値を取得する */
                    String propValue = property.getTextContent();

                    /* setterを使って値を Bean にセットする */
                    String fieldName = property.getAttribute("name");  // name 属性
                    Field beanField = null;
                    try {
                        beanField = beanClazz.getField(fieldName);
                    } catch(NoSuchFieldException | SecurityException e) {
                        e.toString();
                        continue;
                    }

                    StringBuilder sb = new StringBuilder("set");
                    String fName = beanField.getName();
                    StringBuilder tail = new StringBuilder(fName).replace(0, 1, String.valueOf(fName.charAt(0)));
                    String setterName = sb.append(tail).toString();

                    Method setterMethod = null;
                    try {
                        setterMethod = beanInstance.getClass().getMethod(setterName);
                    } catch (NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }
                    String fTypeName = beanField.getType().getTypeName();
                    try {
                        setterMethod.setAccessible(true);
                        if(fTypeName.equals("java.lang.String")) {
                            setterMethod.invoke(beanInstance, propValue);
                        } else if(fTypeName.equals("int")) {
                            setterMethod.invoke(beanInstance, Integer.parseInt(propValue));
                        } else if(fTypeName.equals("long")) {
                            setterMethod.invoke(beanInstance, Long.parseLong(propValue));

                        } else {
                            /* 当面は int と String のみを想定する（後で拡張することは容易）
                             * （一応このクラスでは long も用意してみた）
                             */
                            System.out.println("Not Handrable Type");
                            continue;
                        }
                    } catch(NumberFormatException e) {
                        e.toString();
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.toString();
                    }
                    /* setter を使った値のセット  ここまで */
                }
            } // childList loop end
        } else {
            System.out.println("Error");
        }


        /* Bean が init メソッドを備えていれば、それを呼び出す
         * （多少いびつな実装かもしれないが、ひとまずこの形にしておく）
         */
        Method[] methods = beanClazz.getMethods();  // getMethod によるtry ... catch は好ましくない
        for(Method mElem : methods) {
            if(mElem.getName().equals(INIT_METHOD)) {
                mElem.setAccessible(true);  // 不要だが害はない
                try {
                    mElem.invoke(beanInstance);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return beanInstance;
    }

}
