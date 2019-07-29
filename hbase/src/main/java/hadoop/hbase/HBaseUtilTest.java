package hadoop.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HBaseUtilTest {
    private Admin admin;
    private Connection connection;
    private Configuration conf;
    private TableName tableName=TableName.valueOf("qq61_namespace:test");

    public HBaseUtilTest() throws IOException {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "10.1.46.33");
//        conf.set("hbase.zookeeper.quorum", "10.1.46.33,10.1.46.34,10.1.46.35"); 也可以这样设置
        connection= ConnectionFactory.createConnection(conf);
        admin= connection.getAdmin();
    }

    public static void main(String args[]) throws IOException {
        HBaseUtilTest test=new HBaseUtilTest();
//        test.createTable();
//        test.insert();
//        test.delete();
//        test.deleteList();
        test.getAllTables();
//        test.getRow();
//        test.scanRows();
//        test.closeConn();
    }

    private void closeConn() throws IOException {
        connection.close();
    }

    private void createTable() throws IOException {
        if(admin.tableExists(tableName)){
            System.out.println(tableName+"已存在");
            return;
        }
        HTableDescriptor descriptor=new HTableDescriptor(tableName);
        descriptor.addFamily(new HColumnDescriptor("article"));
        descriptor.addFamily(new HColumnDescriptor("author"));
        admin.createTable(descriptor);
    }

    private void insert() throws IOException {
        Table table=connection.getTable(tableName);
        List<Put> putList=new ArrayList<Put>();
        for (int i = 1; i < 11; i++) {
            Put put=new Put(Bytes.toBytes("rk00"+i));
            put.addColumn(Bytes.toBytes("author"),Bytes.toBytes("name"),Bytes.toBytes("Tom"+i));
            put.addColumn(Bytes.toBytes("article"),Bytes.toBytes("name"),Bytes.toBytes("Hadoop从入门到放弃"+i));
            put.addColumn(Bytes.toBytes("article"),Bytes.toBytes("tag"),Bytes.toBytes("tag"+i));
            if(i%3==0)
                continue;
            put.addColumn(Bytes.toBytes("article"),Bytes.toBytes("content"),Bytes.toBytes(i+""));
            putList.add(put);
        }
        table.put(putList);
        table.close();
    }

    private void delete() throws IOException {
        Table table=connection.getTable(tableName);
        Delete delete=new Delete(Bytes.toBytes("rk001"));
        table.delete(delete);
        table.close();
    }

    private void deleteList() throws IOException {
        Table table=connection.getTable(tableName);
        List<Delete> deleteList=new ArrayList<Delete>();
        for (int i = 1; i < 11; i++) {
            Delete delete=new Delete(Bytes.toBytes("rk00"+i));
            deleteList.add(delete);
        }
        table.delete(deleteList);
        table.close();
    }

    private void deleteTable() throws IOException {
        if(admin.tableExists(tableName)){
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        }else {
            System.out.println("表"+tableName+"不存在");
        }
    }

    private void getAllTables() throws IOException {
        if (admin != null) {
                HTableDescriptor[] allTable = admin.listTables();
                for (HTableDescriptor hTableDescriptor : allTable) {
                    String tableName=hTableDescriptor.getNameAsString();
                    System.out.println(tableName);
                }
        }
    }

    private void getRow() throws IOException {
        Table table=connection.getTable(tableName);
        Get get=new Get(Bytes.toBytes("rk001"));
        Result result=table.get(get);
        for(Cell cell:result.rawCells()){
            System.out.println("RowKey："+ new String(CellUtil.cloneRow(cell)));
            System.out.println("CloumnFamily："+ new String(CellUtil.cloneFamily(cell)));
            System.out.println("Cloumn："+ new String(CellUtil.cloneQualifier(cell)));
            System.out.println("Value："+ new String(CellUtil.cloneValue(cell)));
            System.out.println("Timestamp："+ cell.getTimestamp());
            System.out.println("\n-----------");
        }
        table.close();
    }

    private void scanRows() throws IOException {
        Table table=connection.getTable(tableName);
        Scan scan=new Scan();
        ResultScanner results=table.getScanner(scan);
        for(Result result:results){
            for(Cell cell:result.rawCells()){
                System.out.println("RowKey："+ new String(CellUtil.cloneRow(cell)));
                System.out.println("CloumnFamily："+ new String(CellUtil.cloneFamily(cell)));
                System.out.println("Cloumn："+ new String(CellUtil.cloneQualifier(cell)));
                System.out.println("Value："+ new String(CellUtil.cloneValue(cell)));
                System.out.println("Timestamp："+ cell.getTimestamp());
                System.out.println("\n-----------");
            }
        }
        results.close();
        table.close();
    }
}
