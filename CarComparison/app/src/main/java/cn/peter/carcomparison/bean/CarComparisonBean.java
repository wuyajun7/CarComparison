package cn.peter.carcomparison.bean;

import java.util.List;

public class CarComparisonBean {

    private String search;
    private List<ParamEntity> param;
    private List<ConfigEntity> config;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<ParamEntity> getParam() {
        return param;
    }

    public void setParam(List<ParamEntity> param) {
        this.param = param;
    }

    public List<ConfigEntity> getConfig() {
        return config;
    }

    public void setConfig(List<ConfigEntity> config) {
        this.config = config;
    }

    public static class ParamEntity {
        private String name;
        private List<ParamitemsEntity> paramitems;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ParamitemsEntity> getParamitems() {
            return paramitems;
        }

        public void setParamitems(List<ParamitemsEntity> paramitems) {
            this.paramitems = paramitems;
        }

        public static class ParamitemsEntity {
            /**
             * name : 车型名称
             * valueitems : [{"specid":25379,"value":"POLO 2016款 1.4L 手动风尚型"},{"specid":25381,"value":"POLO 2016款 1.6L 手动舒适型"}]
             */

            private String name;
            private List<ValueitemsEntity> valueitems;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ValueitemsEntity> getValueitems() {
                return valueitems;
            }

            public void setValueitems(List<ValueitemsEntity> valueitems) {
                this.valueitems = valueitems;
            }

            public static class ValueitemsEntity {
                /**
                 * specid : 25379
                 * value : POLO 2016款 1.4L 手动风尚型
                 */

                private int specid;
                private String value;

                public int getSpecid() {
                    return specid;
                }

                public void setSpecid(int specid) {
                    this.specid = specid;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }

    public static class ConfigEntity {

        private String name;
        private List<ConfigitemsEntity> configitems;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ConfigitemsEntity> getConfigitems() {
            return configitems;
        }

        public void setConfigitems(List<ConfigitemsEntity> configitems) {
            this.configitems = configitems;
        }

        public static class ConfigitemsEntity {

            private String name;
            private List<ValueitemsEntityX> valueitems;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ValueitemsEntityX> getValueitems() {
                return valueitems;
            }

            public void setValueitems(List<ValueitemsEntityX> valueitems) {
                this.valueitems = valueitems;
            }

            public static class ValueitemsEntityX {
                /**
                 * specid : 25379
                 * value : 主●&nbsp;/&nbsp;副●
                 */

                private int specid;
                private String value;

                public int getSpecid() {
                    return specid;
                }

                public void setSpecid(int specid) {
                    this.specid = specid;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}
