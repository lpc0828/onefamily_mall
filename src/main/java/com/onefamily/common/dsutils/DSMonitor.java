package com.onefamily.common.dsutils;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DSMonitor {
	private static Map<String, MonitorItem> items = new ConcurrentHashMap<String, MonitorItem>();
	private static Map<String, Long> currentItems = new HashMap<String, Long>();
	private static long lastUpdate = 0L;
	private static Timer dbcpConnMonitorScheduler = new Timer("DBCPConnMonitorScheduler", true);

	static {
		// 每一秒坚持查一次数据库连接池的数据,累计存储, 读取时候获取存储的平均值并清空
		dbcpConnMonitorScheduler.schedule(new DBCPConnMonitorPerSecTimer(), 0L, 1000L);
		//
		dbcpConnMonitorScheduler.schedule(new DBCPConnDataDumpTimer(), 0L, 2000L);
	}

	private static class DBCPConnDataDumpTimer extends TimerTask {
		public void run() {
			long current = System.currentTimeMillis();
			// 距离上次检查间隔50秒内, 跳出
			if (current - lastUpdate < 50000L) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(current);
			// 当前秒数大于10,直接跳出,也就是在每分钟的10秒内如果失败可以重试5次,如果成功, 则lastUpdate小于50秒
			if (cal.get(Calendar.SECOND) > 10) {
				return;
			}
			Map<String, Long> ret = new HashMap<String, Long>();
			for (Entry<String, MonitorItem> entry : items.entrySet()) {
				String name = entry.getKey();
				MonitorItem item = entry.getValue().dumpAndClearItem();
				long connAvgQtyPerSec = item.time > 0L ? Long.valueOf(item.count / item.time) : 0L;
				ret.put(name.replaceAll(" ", "_") + "_Count", new Long(connAvgQtyPerSec));
			}
			//System.err.println("DUMP:" + ret);

			currentItems = Collections.unmodifiableMap(ret);
		}
	}

	private static class DBCPConnMonitorPerSecTimer extends TimerTask {
		public void run() {
			/*
			 * 每秒运行一次, 获取数据库连接池的名字, 连接数量, 存储在Map, Key为poolName, Value为连接数量和取样次数,
			 * 获取的时候, 用数量/取样次数, 获取到平均连接数 当周期获取一次的时候, 就获取到每周期内的平均连接数
			 */
			Set<String> poolNamesSet = DataSourceManager.getInstance().getPoolNames();
			for (String poolName : poolNamesSet) {
				BasicDataSource bds = (BasicDataSource) DataSourceManager.getInstance().getDataSource(poolName);
				{
					String idleKey = "DS_" + poolName + "_ConnIdle";
					int idleValue = bds.getNumIdle();
					MonitorItem tmpItem = (MonitorItem) items.get(idleKey);
					if (tmpItem == null) {
						tmpItem = new MonitorItem();
						items.put(idleKey, tmpItem);
					}
					tmpItem.add((long) idleValue, 1L);
				}
				//
				{
					String maxKey = "DS_" + poolName + "_ConnActive";
					int maxValue = bds.getNumActive();
					MonitorItem tmpItem = (MonitorItem) items.get(maxKey);
					if (tmpItem == null) {
						tmpItem = new MonitorItem();
						items.put(maxKey, tmpItem);
					}
					tmpItem.add((long) maxValue, 1L);
				}
			}
			//System.err.println("SecCheck:" + items);

		}
	}

	public static Map<String, Long> getValues() {
		return currentItems;
	}

	private static class MonitorItem {
		private long count;
		private long time;

		public synchronized void add(long count, long time) {
			this.count = this.count + count;
			this.time = this.time + time;
		}

		public synchronized MonitorItem dumpAndClearItem() {
			MonitorItem item = new MonitorItem();
			item.count = this.count;
			item.time = this.time;
			this.count = 0L;
			this.time = 0L;
			return item;
		}

		@Override
		public String toString() {
			return "MonitorItem [count=" + count + ", time=" + time + "]";
		}

	}//

}
