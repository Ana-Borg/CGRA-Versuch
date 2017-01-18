cd log
echo
echo Converting GraphViz files...
echo
for i in *.dot; do echo $i; dot -Tpdf $i -o $i.pdf; done
echo
echo
echo Converting EPS files...
echo
for i in *.eps; do echo $i; epspdf $i; done
echo
echo All done

