// Helper function for computing the cumulative dot product up to index j.
// Preconditions to ensure safe access and a decreases clause to prove termination.
function sumProd(a: seq<int>, b: seq<int>, j: int): int
  requires 0 <= j < |a|
  requires |a| == |b|
  decreases j
{
  if j == 0 then a[0] * b[0]
  else sumProd(a, b, j - 1) + a[j] * b[j]
}

method dot(a: array?<int>, b: array?<int>) returns (c: array?<int>)
  requires a != null && b != null
  requires a.Length == b.Length && a.Length > 0
  ensures c != null && c.Length == a.Length
  ensures c[0] == a[0] * b[0]
  ensures forall k :: 0 < k < a.Length ==> c[k] == a[k] * b[k] + c[k - 1]
{
  c := new int[a.Length];
  c[0] := a[0] * b[0];
  var i := 1;
  while i < a.Length
    invariant 1 <= i <= a.Length
    invariant c != null && c.Length == a.Length
    invariant forall j :: 0 <= j < i ==> c[j] == sumProd(a[..], b[..], j)
    decreases a.Length - i
  {
    c[i] := c[i - 1] + a[i] * b[i];
    i := i + 1;
  }
}
