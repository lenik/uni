
/* MEMORY MANAGEMENT? */

class mm_base_t {
public:
    virtual void dtor();
};

class user_t : public mm_base_t {
public:
    virtual void dtor();
    virtual user_t *clone();
};

class node_t : public mm_base_t, virtual public user_t {
public:
    virtual node_t *prev() { return NULL; }
    virtual node_t *next() { return NULL; }
    virtual node_t *parent() { return NULL; }
    virtual node_t *child() { return NULL; }
    virtual edge_t *in() { return NULL; }
    virtual edge_t *out() { return NULL; }

    virtual node_t *first() { node_t *c = this; while (c->prev()) c = c->prev(); return c; }
    virtual node_t *last() { node_t *c = this; while (c->next()) c = c->next(); return c; }
    virtual node_t *root() { node_t *c = this; while (c->parent()) c = c->parent(); return c; }
    virtual node_t *leaf() { node_t *c = this; while (c->child()) c = c->child(); return c; }
    virtual node_t *sibling(int index);
    virtual node_t *descent(int index);
    virtual void insert_before(node_t *);
    virtual void insert_after(node_t *);
    virtual void insert_up(node_t *);
    virtual void insert_down(node_t *);
    virtual void remove();
    virtual void remove_before();
    virtual void remove_after();
    virtual void remove_up();
    virtual void remove_down();

    /* memory */
    virtual node_t *clone();
};


class edge_t : virtual public node_t {
public:
    virtual node_t *source() = 0;
    virtual node_t *target() = 0;

    inline edge_t *out_prev() { return static_cast<edge_t *> prev(); }
    inline edge_t *out_next() { return static_cast<edge_t *> next(); }
    inline edge_t *in_prev() { return static_cast<edge_t *> parent(); }
    inline edge_t *in_next() { return static_cast<edge_t *> child(); }
};

class list_node_t : public node_t {

}
