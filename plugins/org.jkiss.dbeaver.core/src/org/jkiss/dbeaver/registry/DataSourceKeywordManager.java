/*
 * Copyright (c) 2011, Serge Rieder and others. All Rights Reserved.
 */

package org.jkiss.dbeaver.registry;

import org.jkiss.dbeaver.ui.editors.sql.SQLConstants;
import org.jkiss.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jkiss.dbeaver.model.*;
import org.jkiss.dbeaver.model.struct.DBSDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Keyword manager.
 * <p/>
 * Contains information about some concrete datasource underlying database syntax.
 * Support runtime change of datasource (reloads syntax information)
 */
public class DataSourceKeywordManager implements DBPKeywordManager {
    static final Log log = LogFactory.getLog(DataSourceKeywordManager.class);

    private TreeMap<String, DBPKeywordType> allKeywords = new TreeMap<String, DBPKeywordType>();

    private TreeSet<String> reservedWords = new TreeSet<String>();
    private TreeSet<String> functions = new TreeSet<String>();
    private TreeSet<String> types = new TreeSet<String>();
    private TreeSet<String> tableQueryWords = new TreeSet<String>();
    private TreeSet<String> columnQueryWords = new TreeSet<String>();

    private String[] singleLineComments = {"--"};

    public DataSourceKeywordManager(DBPDataSource dataSource)
    {
        loadSyntax(dataSource);
    }

    public Set<String> getReservedWords()
    {
        return reservedWords;
    }

    public Set<String> getFunctions()
    {
        return functions;
    }

    public TreeSet<String> getTypes()
    {
        return types;
    }

    public DBPKeywordType getKeywordType(String word)
    {
        return allKeywords.get(word.toUpperCase());
    }

    public List<String> getMatchedKeywords(String word)
    {
        word = word.toUpperCase();
        List<String> result = new ArrayList<String>();
        for (String keyword : allKeywords.tailMap(word).keySet()) {
            if (keyword.startsWith(word)) {
                result.add(keyword);
            } else {
                break;
            }
        }
        return result;
    }

    public boolean isKeywordStart(String word)
    {
        SortedMap<String, DBPKeywordType> map = allKeywords.tailMap(word);
        return !map.isEmpty() && map.firstKey().startsWith(word);
    }

    public boolean isTableQueryWord(String word)
    {
        return tableQueryWords.contains(word.toUpperCase());
    }

    public boolean isColumnQueryWord(String word)
    {
        return columnQueryWords.contains(word.toUpperCase());
    }

    void loadSyntax(final DBPDataSource dataSource)
    {
        DBPDataSourceInfo dataSourceInfo = dataSource.getInfo();
        allKeywords.clear();
        reservedWords.clear();
        functions.clear();
        types.clear();

        // Add default set of keywords
        Collections.addAll(reservedWords, SQLConstants.SQL92_KEYWORDS);
        Collections.addAll(reservedWords, SQLConstants.SQL_EX_KEYWORDS);
        Collections.addAll(tableQueryWords, SQLConstants.TABLE_KEYWORDS);
        Collections.addAll(columnQueryWords, SQLConstants.COLUMN_KEYWORDS);

        try {
            // Keywords
            Collection<String> sqlKeywords = dataSourceInfo.getSQLKeywords();
            if (!CommonUtils.isEmpty(sqlKeywords)) {
                for (String keyword : sqlKeywords) {
                    reservedWords.add(keyword.toUpperCase());
                }
            }
            final Collection<String> executeKeywords = dataSourceInfo.getExecuteKeywords();
            if (!CommonUtils.isEmpty(executeKeywords)) {
                for (String keyword : executeKeywords) {
                    reservedWords.add(keyword.toUpperCase());
                }
            }

            // Functions
            Set<String> allFunctions = new HashSet<String>();
            if (dataSourceInfo.getNumericFunctions() != null) {
                for (String func : dataSourceInfo.getNumericFunctions()) {
                    allFunctions.add(func.toUpperCase());
                }
            }
            if (dataSourceInfo.getStringFunctions() != null) {
                for (String func : dataSourceInfo.getStringFunctions()) {
                    allFunctions.add(func.toUpperCase());
                }
            }
            if (dataSourceInfo.getSystemFunctions() != null) {
                for (String func : dataSourceInfo.getSystemFunctions()) {
                    allFunctions.add(func.toUpperCase());
                }
            }
            if (dataSourceInfo.getTimeDateFunctions() != null) {
                for (String func : dataSourceInfo.getTimeDateFunctions()) {
                    allFunctions.add(func.toUpperCase());
                }
            }
            functions.addAll(allFunctions);

            // Types
            if (dataSource instanceof DBPDataTypeProvider) {
                Collection<? extends DBSDataType> supportedDataTypes = ((DBPDataTypeProvider)dataSource).getDataTypes();
                if (supportedDataTypes != null) {
                    for (DBSDataType dataType : supportedDataTypes) {
                        types.add(dataType.getName().toUpperCase());
                    }
                }
            }
            if (types.isEmpty()) {
                // Add default types
                Collections.addAll(types, SQLConstants.DEFAULT_TYPES);
            }

            functions.addAll(allFunctions);
        }
        catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException)e).getTargetException();
            }
            log.error(e);
        }

        // Remove types and functions from reserved words list
        reservedWords.removeAll(types);
        reservedWords.removeAll(functions);

        addKeywords(reservedWords, DBPKeywordType.KEYWORD);
        addKeywords(functions, DBPKeywordType.FUNCTION);
        addKeywords(types, DBPKeywordType.TYPE);
    }

    private void addKeywords(Set<String> set, DBPKeywordType type)
    {
        for (String keyword : set) {
            allKeywords.put(keyword, type);
        }
    }

    public String[] getSingleLineComments()
    {
        return singleLineComments;
    }

}
