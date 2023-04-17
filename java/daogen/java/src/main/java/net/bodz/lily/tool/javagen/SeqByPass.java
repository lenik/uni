package net.bodz.lily.tool.javagen;

import java.util.Objects;

public class SeqByPass {

    public int pass;
    public int seq;

    public SeqByPass(int pass, int seq) {
        this.pass = pass;
        this.seq = seq;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pass, seq);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SeqByPass other = (SeqByPass) obj;
        return pass == other.pass && seq == other.seq;
    }

    @Override
    public String toString() {
        return pass + "," + seq;
    }

}
