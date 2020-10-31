package gudusoft.gsqlparser.dlineage.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import gudusoft.gsqlparser.dlineage.json.JSONException;

@SuppressWarnings("rawtypes")
public abstract class BeanUtils {

	private static final Map<String, PropertyDescriptor[]> classPropertyDescriptorMap = new WeakHashMap<String, PropertyDescriptor[]>();

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> bean2Map(Object sourceBean) {
		if (sourceBean instanceof LinkedHashMap) {
			return (LinkedHashMap<String, Object>) sourceBean;
		}

		try {
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			String className = sourceBean.getClass().getCanonicalName();
			if (!classPropertyDescriptorMap.containsKey(className)) {
				final Map<String, Integer> sortMap = sortFieldsMap(sourceBean.getClass());
				PropertyDescriptor[] descriptors = Introspector.getBeanInfo(sourceBean.getClass())
						.getPropertyDescriptors();
				List<PropertyDescriptor> filterDescriptors = new ArrayList<PropertyDescriptor>();
				for (int i = 0; i < descriptors.length; i++) {
					PropertyDescriptor descriptor = descriptors[i];
					if (!descriptor.getName().equals("class") && descriptor.getReadMethod() != null) {
						filterDescriptors.add(descriptor);
					}
				}

				Collections.sort(filterDescriptors, new Comparator<PropertyDescriptor>() {
					public int compare(PropertyDescriptor d1, PropertyDescriptor d2) {
						return sort(sortMap, d1.getName(), d2.getName());
					}
				});
				classPropertyDescriptorMap.put(className, filterDescriptors.toArray(new PropertyDescriptor[0]));
			}

			PropertyDescriptor[] descriptors = classPropertyDescriptorMap.get(className);
			for (PropertyDescriptor descriptor : descriptors) {
				try {
					Object tempObj = descriptor.getReadMethod().invoke(sourceBean);
					if (tempObj == null)
						continue;
					if (tempObj.getClass().isMemberClass())
						map.put(descriptor.getName(), bean2Map(tempObj));
					else if (tempObj instanceof Collection) {
						List maps = new ArrayList();
						Collection collection = (Collection) tempObj;
						for (Object obj : collection) {
							maps.add(bean2Map(obj));
						}
						map.put(descriptor.getName(), maps);
					} else
						map.put(descriptor.getName(), tempObj);
				} catch (Exception e) {
					throw new JSONException(e);
				}
			}
			return map;
		} catch (IntrospectionException e) {
			throw new JSONException(e);
		}
	}

	private static Map<String, Integer> sortFieldsMap(Class targetClass) {
		Map<String, Integer> sortMap = new HashMap<String, Integer>();
		Field[] fields = targetClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			sortMap.put(field.getName(), sortMap.size());
		}
		return sortMap;
	}

	private static int sort(Map<String, Integer> sortMap, String firstName, String secondName) {
		if (sortMap.containsKey(firstName) && sortMap.containsKey(secondName))
			return sortMap.get(firstName).compareTo(sortMap.get(secondName));
		return 0;
	}

}
