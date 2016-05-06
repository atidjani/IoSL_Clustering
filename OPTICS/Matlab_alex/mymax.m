%{
fileID = fopen('1.txt','r');
formatSpec = '%f';
sizeA = [10 3];
points = fscanf(fileID,formatSpec,sizeA)
size(points')
fclose(fileID);
%}

filename = '1.txt';
delimiterIn = ' ';
headerlinesIn = 0;
points = importdata(filename,delimiterIn,headerlinesIn);
points