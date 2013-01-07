// @file     Matrix.java
// @author   Mac Radigan

package org.radigan.system.utilities;

import java.util.List;

public interface Matrix {
  public Matrix pow(int n);
  public double eigW(int index);
  public Matrix transpose();
  public String toString();
  public Matrix mult(Matrix m);
  public Matrix multElementwise(Matrix m);
  public Matrix add(Matrix m);
  public Matrix signum();
  public Matrix gt(double th);
  public Matrix lt(double th);
  public Matrix abs();
  public int lastNonzeroRow();
  public Matrix fill(Matrix m);
  public Matrix min();
  public Matrix max();
  public Matrix sum();
  public Matrix land(Matrix a);
  public List<Integer> indToRows(List<Integer> ind);
  public List<Integer> indToCols(List<Integer> ind);
  public List<Integer> find();
  public List<Integer> find(List<Integer> rows, List<Integer> cols);
  public int getRows();
  public List<Integer> getRowRange();
  public int getCols();
  public List<Integer> getColRange();
  public double getEntry(int row, int col);
  public void setEntry(int row, int col, double val);
  public List<Matrix> qr(Matrix a);
  public Matrix solve(Matrix b);
  public Matrix inverse();
  public Matrix sub(List<Integer> rows, List<Integer> cols);
  public List<Integer> size();
}

// *EOF*
