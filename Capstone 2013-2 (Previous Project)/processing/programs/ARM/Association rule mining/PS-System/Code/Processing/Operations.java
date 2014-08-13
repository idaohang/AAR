/**
 * Operations class.
 * 
 * Start Date: 17 June 2008
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import java.util.ArrayList;

public class Operations {
	
	private int l1, l2;

	/**
	 * subset method.
	 * Method used to determine if one set is a subset of the other.
	 * In this case, is set2 a subset of set1.
	 */
	final protected boolean subset(int[] set1, int[] set2){
		boolean matched = false;
		l1 = set1.length;
		l2 = set2.length;

		if (l2 > l1){
			return false;
		}
		else{
			for (int i = 0; i < l2; i++){
				for (int j = 0; j < l1; j++){
					if (set2[i] == set1[j]){
						matched = true;
						break;
					}
					else{
						matched = false;
					}
				}
				if (!matched){
					break;
				}
			}
			return matched;
		}
	}
	
	/**
	 * subsetNE method.
	 * Method used to determine if one set is a subset of the other, but
	 * it must also be smaller in the number of items and not the same set,
	 * thus s1 subset s1 will fail (because it they are equal).
	 * In this case, is set2 a subset of set1.
	 */
	final protected boolean subsetNE(int[] set1, int[] set2){
		boolean matched = false;
		l1 = set1.length;
		l2 = set2.length;

		if (l1 <= l2){
			return false;
		}
		else{
			for (int i = 0; i < l2; i++){
				for (int j = 0; j < l1; j++){
					if (set2[i] == set1[j]){
						matched = true;
						break;
					}
					else{
						matched = false;
					}
				}
				if (!matched){
					break;
				}
			}
			return matched;
		}
	}
	
	/**
	 * supset method.
	 * Method used to determine if one set is a supset of the other.
	 * In this case, is set1 a supset of set2.
	 */
	final protected boolean supset(int[] set1, int[] set2){
		boolean matched = false;
		l1 = set1.length;
		l2 = set2.length;

		if (l1 <= l2){
			//If set1 smaller or equal in length then not a supset.
			return false;
		}
		else{
			for (int i = 0; i < l2; i++){
				for (int j = 0; j < l1; j++){
					if (set2[i] == set1[j]){
						matched = true;
						break;
					}
					else{
						matched = false;
					}
				}
				if (!matched){
					break;
				}
			}
			return matched;
		}
	}

	/**
	 * supsetE method.
	 * Method used to determine if one set is a supset of the other.
	 * In this case, is set1 a supset of set2. This method also allows
	 * set1 to be a supset of set2 when they are identical/equal.
	 */
	final protected boolean supsetE(int[] set1, int[] set2){
		boolean matched = false;
		l1 = set1.length;
		l2 = set2.length;

		if (l1 < l2){
			//If set1 is smaller in length then not a supset.
			return false;
		}
		else{
			for (int i = 0; i < l2; i++){
				for (int j = 0; j < l1; j++){
					if (set2[i] == set1[j]){
						matched = true;
						break;
					}
					else{
						matched = false;
					}
				}
				if (!matched){
					break;
				}
			}
			return matched;
		}
	}
	
	/**
	 * negSubset method.
	 * Method used to remove a subset contents from a supset.
	 * in this case, set2 is removed from set1. ( s1 \ s2 )
	 */
	final protected int[] negSubset(int[] part1, int[] part2){
		int[] rule;
		ArrayList temp = new ArrayList();
		boolean found = false;
		l1 = part1.length;
		l2 = part2.length;
		for (int i = 0; i < l1; i++){
			for (int j = 0; j < l2; j++){
				if (part2[j] == part1[i]){
					found = true;
					break;
				}
			}
			if (!found){
				temp.add(part1[i]);
			}
			found = false;
		}
		l1 = temp.size();
		rule = new int[l1];
		for (int i = 0; i< l1; i++){
			rule[i] = (Integer)temp.get(i);
		}
		return rule;
	}

	/**
	 * unionRule method.
	 * method used to perform the union operation between
	 * the contents of two sets. Thus the contents of set1
	 * and set2 are added together to form one set which
	 * holds the contents of both without any duplication.
	 * ( s1 U s2 )
	 */
	final protected int[] unionRule(int[] part1, int[] part2){
		int[] rule;
		ArrayList ruleList = new ArrayList();
		l1 = part1.length;
		l2 = part2.length;

		for (int i = 0; i < l1/*part1.length*/; i++){
			ruleList.add(part1[i]);
		}
		for (int i = 0; i < l2/*part2.length*/; i++){
			if (ruleList.indexOf(part2[i]) == -1){
				ruleList.add(part2[i]);
			}
		}
		l1 = ruleList.size();
		rule = new int[l1];
		for (int i = 0; i < l1; i++){
			rule[i] = (Integer)ruleList.get(i);
		}
		return rule;
	}
	
	final protected int[] union(ArrayList ids){
		int[] items, temp;
		ArrayList itemList = new ArrayList();
		int i1, i2;
		i1 = ids.size();
		for (int i = 0; i < i1; i++){
			temp = (int[])ids.get(i);
			i2 = temp.length;
			for (int j = 0; j < i2; j++){
				if (itemList.indexOf(temp[j]) == -1){
					itemList.add(temp[j]);
				}
			}
		}
		i1 = itemList.size();
		items = new int[i1];
		for (int i = 0; i < i1; i++){
			items[i] = (Integer)itemList.get(i);
		}
		return items;
	}
	
	/**
	 * removeGen method.
	 * Method used to remove the contents of a generator
	 * from the corresponding set. The generator items are
	 * the first set and the set that they are to be removed
	 * from is the second one. Similar to the negSubset
	 * method. ( f \ g )
	 */
	final protected int[] removeGen(int[] gen, int[] item){
		boolean match = false;
		int[] rule;
		ArrayList temp = new ArrayList();
		l1 = gen.length;
		l2 = item.length;

		for (int i = 0; i < l2; i++){
			for (int j = 0; j < l1; j++){
				if (item[i] == gen[j]){
					match = true;
					//j = genParts.length;
					break;
				}
			}
			if (match == false){
				//This part is not a generator...
				temp.add(item[i]);
			}
			match = false;
		}
		l1 = temp.size();
		rule = new int[l1];
		for (int i = 0; i < l1; i++){
			rule[i] = (Integer)temp.get(i);
		}
		return rule;
	}
	
	final protected boolean compare(int[] entry1, int[] entry2){
		boolean match = false;
		l1 = entry1.length;
		l2 = entry2.length;
		if (l1 == l2){
			for (int i = 0; i < l1; i++){
				match = false;
				for (int j = 0; j < l2; j++){
					if (entry1[i] == entry2[j]){
						match = true;
						break;
					}
				}
				if (!match){
					return false;
				}
			}
			if (match){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * determineSubsets method.
	 * Method used to oversee the determination of all of the
	 * possible subsets of a given set. ( findall s  s1 )
	 */
	final protected ArrayList determineSubsets(int[] parts){
		ArrayList subsets = new ArrayList();
		int[] subset;
		int[] item = new int[1];
		int length = 1;
		l1 = parts.length;
		int subLength = l1 - 1;

		while (length <= subLength){
			for (int i = 0; i < l1; i++){
				item[0] = parts[i];
				if (length == 1){
					//Length 1 subsets...
					subset = new int[1];
					subset[0] = item[0];
					subsets.add(subset);
				}
				else{
					determineSubsets(length, i, 1, item, parts, subsets);
				}
			}
			length++;
		}
		return subsets;
	}

	/**
	 * determineSubsets method.
	 * Recursive method used to work through all and determine
	 * all of the possible subsets of a given set.
	 */
	final protected void determineSubsets(int length, int i, int cl, int[] item, int[] parts, ArrayList subsets){
		int[] subset;
		int size = item.length;
		l1 = parts.length;

		if ((cl + 1) < l1){
			if ((cl + 1) == length){
				for (int j = (i + 1); j < l1; j++){
					subset = new int[size + 1];
					for (int k = 0; k < size; k++){
						subset[k] = item[k];
					}
					subset[size] = parts[j];
					subsets.add(subset);
				}
			}
			else{
				for (int j = (i + 1); j < l1; j++){
					subset = new int[size + 1];
					for (int k = 0; k < size; k++){
						subset[k] = item[k];
					}
					subset[size] = parts[j];
					determineSubsets(length, j, (cl + 1), subset, parts, subsets);
				}
			}
		}
	}
	
	/**
	 * Method used to test to see if one set of items are the children
	 * of another set of items or that the same item is in both sets if not a child.
	 * Thus test to see if all the items in set1 are children of the items in set2.
	 */
	final protected boolean child(Object[] AbstractTable, int[] set1, int[] set2){
		boolean child = false;
		int size1, size2;
		int[] abstractAttIDs = (int[])AbstractTable[1];
		Object[] abstractDescLists = (Object[])AbstractTable[2];
		size1 = set1.length;
		size2 = set2.length;
		for (int i = 0; i < size1; i++){
			child = false;
			for (int j = 0; j < size2; j++){
				if (set1[i] == set2[j] || isChild(set1[i], set2[j], abstractAttIDs, abstractDescLists)){
					child = true;
					break;
				}
			}
			if (!child){
				return false;
			}
		}
		return child;
	}
	
	final protected boolean isChild(int set1, int set2, int[] list1, Object[] list2){
		int size1, size2;
		int[] descAtts;
		size1 = list1.length;
		for (int i = 0; i < size1; i++){
			if (set2 == list1[i]){
				descAtts = (int[])list2[i];
				size2 = descAtts.length;
				for (int j = 0; j < size2; j++){
					if (descAtts[j] == set1){
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * Method used to test to see if all the items in one set are a parent of at least
	 * one item in a second set or that the same item is in both sets if not a parent.
	 * Thus test to see if all the items in set1 are a parent of the items in set2.
	 */
	final protected boolean parent(Object[] AbstractTable, int[] set1, int[] set2){
		boolean parent = false;
		int size1, size2;
		int[] abstractAttIDs = (int[])AbstractTable[1];
		Object[] abstractDescLists = (Object[])AbstractTable[2];
		size1 = set1.length;
		size2 = set2.length;
		for (int i = 0; i < size1; i++){
			parent = false;
			for (int j = 0; j < size2; j++){
				if (set2[j] == set1[i] || isParent(set1[i], set2[j], abstractAttIDs, abstractDescLists)){
					parent = true;
					break;
				}
			}
			if (!parent){
				return false;
			}
		}
		return parent;
	}
	
	final protected boolean isParent(int set1, int set2, int[] list1, Object[] list2){
		int size1, size2;
		int[] descAtts;
		size1 = list1.length;
		for (int i = 0; i < size1; i++){
			if (set1 == list1[i]){
				descAtts = (int[])list2[i];
				size2 = descAtts.length;
				for (int j = 0; j < size2; j++){
					if (descAtts[j] == set2){
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	final protected boolean validRule(Object[] table, int[] a, int[] c){
		boolean related = false;
		int size1, size2;
		int[] abstractAttIDs = (int[])table[1];
		Object[] abstractDescLists = (Object[])table[2];
		size1 = a.length;
		size2 = c.length;
		//Check antecedent against the consequent...
		for (int i = 0; i < size1; i++){
			related = false;
			for (int j = 0; j < size2; j++){
				if (isParent(a[i], c[j], abstractAttIDs, abstractDescLists)){
					related = true;
					break;
				}
				else if (isParent(c[j], a[i], abstractAttIDs, abstractDescLists)){
					related = true;
					break;
				}
			}
			if (related){
				return false;
			}
			//Check this antecedent item against the other antecedent items...
			for (int j = 0; j < size1; j++){
				if (i != j && (isParent(a[i], a[j], abstractAttIDs, abstractDescLists) || isParent(a[j], a[i], abstractAttIDs, abstractDescLists))){
					related = true;
					break;
				}
			}
			if (related){
				return false;
			}
		}
		//Check consequent items against each other...
		for (int i = 0; i < size2; i++){
			for (int j = 0; j < size2; j++){
				if (i != j && (isParent(c[i], c[j], abstractAttIDs, abstractDescLists) || isParent(c[j], c[i], abstractAttIDs, abstractDescLists))){
					related = true;
					break;
				}
			}
			if (related){
				return false;
			}
		}
		return !related;
	}
	
	final protected boolean present(int[] set, int item){
		int size = set.length;
		for (int i = 0; i < size; i++){
			if (set[i] == item){
				//Item is present at this location...
				return true;
			}
		}
		return false;
	}
	
	final protected int present(int[] set, int item, int x){
		int size = set.length;
		for (int i = 0; i < size; i++){
			if (set[i] == item){
				//Item is present at this location...
				return i;
			}
		}
		return -1;
	}
	
	final protected boolean present(ArrayList list, int[] set){
		int size = list.size();
		for (int i = 0; i < size; i++){
			if (compare((int[])list.get(i), set)){
				return true;
			}
		}
		return false;
	}
	
	final protected int present(ArrayList list, int[] set, int x){
		int size = list.size();
		for (int i = 0; i < size; i++){
			if (compare((int[])list.get(i), set)){
				return i;
			}
		}
		return -1;
	}
	
	final protected boolean rulePresent(ArrayList rules, int[] p1, int[] p2){
		int[] r1, r2;
		int size = rules.size();
		int s1, s2, s3, s4;
		
		s1 = p1.length;
		s2 = p2.length;
		for (int i = 0; i < size; i++){
			r1 = (int[])((Object[])rules.get(i))[0];
			r2 = (int[])((Object[])rules.get(i))[1];
			s3 = r1.length;
			s4 = r2.length;
			if (s1 == s3 && s2 == s4){
				if (compare(p1, r1) && compare(p2, r2)){
					return true;
				}
			}
		}
		return false;
	}
	
	final protected int[] xor(int[] set1, int[] set2){
		int[] result;
		ArrayList temp = new ArrayList();
		int length = set1.length;
		for (int i = 0; i < length; i++){
			if (!present(set2, set1[i])){
				//Item in set1 is not in set2...
				temp.add(set1[i]);
			}
		}
		length = set2.length;
		for (int i = 0; i < length; i++){
			if (!present(set1, set2[i])){
				//Item in set2 is not in set1...
				temp.add(set2[i]);
			}
		}
		length = temp.size();
		result = new int[length];
		for (int i = 0; i < length; i++){
			result[i] = (Integer)temp.get(i);
		}
		return result;
	}
}