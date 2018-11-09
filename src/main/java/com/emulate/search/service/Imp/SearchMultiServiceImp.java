package com.emulate.search.service.Imp;

import com.emulate.search.SearchTaskCallable.SingleTaskCallable;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.service.SearchMultiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class SearchMultiServiceImp implements SearchMultiService {

    private final static Logger logger =  LoggerFactory.getLogger(SearchMultiServiceImp.class);

    private static final int FUTURE_TIMEOUT = 5000;
    // 使用先进先出的队列
    public final static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    // 执行callable的线程池
    public static ExecutorService executorService = new ThreadPoolExecutor(5,50 ,10,TimeUnit.SECONDS,queue);

    public List<Map<String,Object>> multilSearchAdapter(RequestInfo requestInfo, CommonDao commonDao){
        Long start=System.currentTimeMillis();
        SingleTaskCallable task=null;
        List<FutureTask> futureTaskList=new ArrayList();
        for (int i=0;i<4;i++){
            task=new SingleTaskCallable();
            task.setCommonDao(commonDao);
            task.setRequestInfo(requestInfo);
            task.setStartOffset(i*30000+1);
            task.setEndOffset(i*30000+30000);
            FutureTask futureTask=new FutureTask(task);
            futureTaskList.add(futureTask);
            //开始获取数据
            executorService.submit(futureTask);
        }
          logger.info("查询耗时："+String.valueOf(System.currentTimeMillis()-start));
        List<Map<String,Object>> DataList = new ArrayList<>();
        for (FutureTask futureTask : futureTaskList) {
            try {
                // 获取数据，注意有超时时间，如果超出，即获取不到数据
                List<Map<String,Object>> resultData = (List<Map<String, Object>>) futureTask.get(FUTURE_TIMEOUT, TimeUnit.MILLISECONDS);
                if(resultData==null||resultData.isEmpty()){
                    break;
                }
                DataList.addAll(resultData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                logger.info("超时了！");
                e.printStackTrace();
            }finally {
                // 最后一定要调用cancel方法，里面的参数 mayInterruptIfRunning 是是否在运行的时候也关闭，如果设置为true，那么在
                // 运行的时候也能关闭，之后的代码不会再执行。
                // 如果正在运行，暂停成功，会返回true，如果运行完了，那么不管 mayInterruptIfRunning 是什么值，都会返回false。
                futureTask.cancel(true);            }
            }
        logger.info("处理耗时："+String.valueOf(System.currentTimeMillis()-start));
        return DataList;
    }
}
