package com.bohui.http

/**
 *
 * User: liyangli
 * Date: 2015/3/4
 * Time: 16:30
 */
URL url = null;
HttpURLConnection http = null;

try {
    url = new URL("http://172.17.5.102:8080/servlet/receiver");
    http = (HttpURLConnection) url.openConnection();
    http.setDoInput(true);
    http.setDoOutput(true);
    http.setUseCaches(false);
    http.setConnectTimeout(50000);//设置连接超时
//如果在建立连接之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。
    http.setReadTimeout(50000);//设置读取超时
//如果在数据可读取之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。
    http.setRequestMethod("POST");
    http.setRequestProperty("Content-Type","text/xml; charset=UTF-8");
//    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    http.connect();
    param = "test";

    OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf-8");
    osw.write(param);
    osw.flush();
    osw.close();

    if (http.getResponseCode() == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            result += inputLine;
        }
        br.close();
        //result = "["+result+"]";
    }
} catch (Exception e) {
    e.printStackTrace();
    System.out.println("err");
} finally {
    if (http != null) http.disconnect();

}