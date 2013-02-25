package eu.kozzi.bp.Tree;

import javax.persistence.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 13.1.13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "NODE")
public class Node {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Integer myValue;


    @ManyToOne
    @JoinColumn(name="parent_id")
    private Node parent;

    @OneToMany(mappedBy="parent", cascade = {CascadeType.REMOVE})
    private List<Node> children = new ArrayList<Node>();


    public Long getId() {
        return this.id;
    }

    public Integer getMyValue() {
        return myValue;
    }

    public void setMyValue(Integer myValue) {
        this.myValue = myValue;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public int getHeight() {
        return this.getHeight(0);
    }

    private int getHeight(int height) {
        int maxHeight = height;
        try {
            Iterator<Node> iterator = this.getChildren().iterator();
            while(iterator.hasNext()) {
                int childHeight = iterator.next().getHeight(height);
                if (childHeight > maxHeight) {
                    maxHeight = childHeight;
                }
            }
        } catch (Exception nexp) {
            nexp.printStackTrace();
        }
        return maxHeight + 1;
    }

    public List<Node> findByValue(int value) {
        List<Node> nodes = new ArrayList<Node>();
        return this.findByValue(value, nodes);
    }

    private List<Node> findByValue(int value, List<Node> nodes) {
        try {
            Iterator<Node> iterator = this.getChildren().iterator();
            while(iterator.hasNext()) {
                nodes = iterator.next().findByValue(value, nodes);
            }
            if (this.getMyValue() == value) {
                nodes.add(this);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return nodes;
    }
}
