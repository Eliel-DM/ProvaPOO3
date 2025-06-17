module elieldm.provapoo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires static lombok;

    exports elieldm.provapoo;

    opens elieldm.provapoo to javafx.fxml;
    opens elieldm.provapoo.controller to javafx.fxml;
    opens elieldm.provapoo.model to javafx.base, org.hibernate.orm.core;
    opens elieldm.provapoo.view to javafx.fxml;
    opens elieldm.provapoo.dao to org.hibernate.orm.core;
    opens elieldm.provapoo.utils to org.hibernate.orm.core;


}
