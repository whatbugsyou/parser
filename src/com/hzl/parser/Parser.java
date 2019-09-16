package com.hzl.parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	/**
	 * 对象到 JSON 序列化
	 * 
	 * @param o
	 * @return
	 */
	public String toJson(Object o) {
		if (o == null) {
			return null;
		} else {
			StringBuilder sb = new StringBuilder();
			Class clazz = o.getClass();
			String type = clazz.getSimpleName();
			if (type.equals("Map") || type.equals("HashMap")) {
				// map对象
				sb.append(mapJson(o));
			} else if (type.equals("List") || type.equals("ArrayList")) {
				// list对象
				sb.append("[");
				List<Object> list = (List<Object>) o;
				for (Object object : list) {
					sb.append(objectJson(object) + ",");
				}
				sb = new StringBuilder(sb.substring(0, sb.lastIndexOf(",")));
				sb.append("]");
			} else {
				// 普通对象
				sb.append(objectJson(o));
			}
			return sb.toString();
		}
	}

	/**
	 * map对象序列化
	 * 
	 * @param o
	 * @return
	 */
	private String mapJson(Object o) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		Map<String, Object> map = (Map<String, Object>) o;
		Set<String> keys = map.keySet();
		for (String key : keys) {
			if (map.get(key) instanceof String) {
				sb.append(String.format("\"%s\":\"%s\",", key, map.get(key)));
			} else {
				sb.append(String.format("\"%s\":%s,", key, map.get(key)));
			}
		}
		sb = new StringBuilder(sb.substring(0, sb.lastIndexOf(",")));
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 普通对象序列化
	 * 
	 * @param o
	 * @return
	 */
	private String objectJson(Object o) {
		if (o == null) {
			return "{}";
		} else {
			Class clazz = o.getClass();
			StringBuilder sb = new StringBuilder();
			Field[] fields = clazz.getDeclaredFields();
			sb.append("{");
			for (Field field : fields) {
				String name = field.getName();
				String name1 = String.format("%s%s", name.substring(0, 1).toUpperCase(), name.substring(1));
				try {
					Method method = clazz.getDeclaredMethod("get" + name1);
					if (method.invoke(o) != null) {
						if ("String".equals(method.getReturnType().getSimpleName())) {
							sb.append(String.format("\"%s\":\"%s\",", name, method.invoke(o)));
						} else if ("int".equals(method.getReturnType().getSimpleName())
								|| "boolean".equals(method.getReturnType().getSimpleName())) {
							sb.append(String.format("\"%s\":%s,", name, method.invoke(o)));
						} else if ("List".equals(method.getReturnType().getSimpleName())
								|| "ArrayList".equals(method.getReturnType().getSimpleName())) {
							StringBuilder strb = new StringBuilder();
							strb.append("[");
							List<Object> tempList = (List<Object>) method.invoke(o);
							for (Object object : tempList) {
								strb.append(objectJson(object) + ",");
							}
							strb = new StringBuilder(strb.substring(0, strb.lastIndexOf(",")));
							strb.append("]");
							sb.append(String.format("\"%s\":%s,", name, strb));
						} else if ("Map".equals(method.getReturnType().getSimpleName())
								|| "HashMap".equals(method.getReturnType().getSimpleName())) {
							String temp = mapJson(method.invoke(o));
							sb.append(String.format("\"%s\":%s,", name, temp));
						} else {
							String temp = objectJson(method.invoke(o));
							sb.append(String.format("\"%s\":%s,", name, temp));

						}
					}
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			sb = new StringBuilder(sb.substring(0, sb.lastIndexOf(",")));
			sb.append("}");
			return sb.toString();
		}
	}

	/**
	 * 反序列化json->object
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public Object fromJson(String json, Class clazz) {
		if ("[".equals(json.substring(0, 1))) {
			// json数据，默认返回类型List
			// 此时传进来的clazz不是返回类型的类对象，而是List集合中泛型的类型对象
			String strs = json.substring(1, json.lastIndexOf("]"));
			List<Object> list = new ArrayList<>();
			String[] array = strs.split(",\\{");
			for (int i = 0; i < array.length; i++) {
				if (i > 0) {
					array[i] = "{" + array[i];
				}
				list.add(fromJson(array[i], clazz));
			}
			return list;
		} else {
			// json对象
			Object o = null;
			try {
				o = clazz.newInstance();
				String reg = "(\\d|[a-z]|:\\{.*?\\}|:\\[\\{.*?\\}\\])+";
				Pattern p = Pattern.compile(reg);
				Matcher m = p.matcher(json);
				List<String> strs = new ArrayList<>();
				while (m.find()) {
					strs.add(m.group());
					// 为了使偶数为字段名，奇数为属性值
					if (m.group().matches(".*:\\{.*|.*:\\[\\{.*")) {
						strs.add("");
					}
				}
				for (int i = 0; i < strs.size(); i++) {
					if (strs.get(i).matches(".*:\\{.*")) {
						// 字段属性为自定义引用类型
						String array = strs.get(i).substring(0, strs.get(i).indexOf(":"));
						String array1 = strs.get(i).substring(strs.get(i).indexOf(":") + 1);
						String name = String.format("%s%s", array.substring(0, 1).toUpperCase(), array.substring(1));
						Method method1 = clazz.getDeclaredMethod("get" + name);
						Method method = clazz.getDeclaredMethod("set" + name, new Class[] { method1.getReturnType() });
						method.invoke(o, fromJson(array1, method1.getReturnType()));
					} else if (strs.get(i).matches(".*:\\[\\{.*")) {
						String array = strs.get(i).substring(0, strs.get(i).indexOf(":"));
						String array1 = strs.get(i).substring(strs.get(i).indexOf(":") + 1);
						String name = String.format("%s%s", array.substring(0, 1).toUpperCase(), array.substring(1));
						Method method1 = clazz.getDeclaredMethod("get" + name);
						Method method = clazz.getDeclaredMethod("set" + name, new Class[] { method1.getReturnType() });
						Field f = clazz.getDeclaredField(array); 
						Class genericClazz = null;
						Type fc = f.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
						if (fc instanceof ParameterizedType) // 如果是泛型参数的类型
						{
							ParameterizedType pt = (ParameterizedType) fc;
							genericClazz = (Class) pt.getActualTypeArguments()[0]; // 得到泛型里的class类型对象。
						}

						method.invoke(o, fromJson(array1, genericClazz));
					} else if (strs.get(i).equals("")) {
						continue;
					} else {
						// 字段属性基本类型和String
						if (i % 2 == 0 || i == 0) {
							String name = String.format("%s%s", strs.get(i).substring(0, 1).toUpperCase(),
									strs.get(i).substring(1));
							Method method1 = clazz.getDeclaredMethod("get" + name);
							Method method = clazz.getDeclaredMethod("set" + name,
									new Class[] { method1.getReturnType() });
							if (method1.getReturnType().getSimpleName().equals("int")) {
								method.invoke(o, Integer.parseInt(strs.get(i + 1)));
							} else if (method1.getReturnType().getSimpleName().equals("String")) {
								method.invoke(o, strs.get(i + 1));
							}
						}
					}

				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return o;
		}
	}

}
