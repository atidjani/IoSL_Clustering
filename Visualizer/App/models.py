import io
import hashlib
import time
import re

from django.db import models
from django.utils.translation import ugettext as _
from django.core.exceptions import ValidationError


# Define the model used to store the dataset in the databse
class Dataset(models.Model):
    creationTime = models.DateTimeField(auto_now_add=True)
    data = models.TextField()
    noise = models.TextField(blank=True)

    # Performs a validation of the dataset uplodaed by the user to
    # check if is in the required format.
    def clean(self) :
        buffer = io.StringIO(self.data.decode("utf-8"))
        for line in buffer.readlines():
            features = line.split(',')

                # Check if 2 elements where extracted.
            if len(features) != 2 :
                raise ValidationError(_('Format not compatible'))

                # Check if all elements are float
            for f in features[:-1]:
                try :
                    float(f)
                except ValueError:
                   raise ValidationError(_('Format not compatible'))
        return True

    # Returns all the points in the dataset
    def getPoints(self) :
        res = self.data
        if self.noise != '' :
            if re.match('[0-9]', self.data[-1]): #Well played dataset 0, well played.
                res += '\n'
            res += self.noise
        return res

    # Write the points in the dataset to a file and returns the name of the file.
    # The name of the file is the md5 of the time.
    def writeFile(self) :
        filePath = "/tmp/" + hashlib.md5(str(time.time()).encode('utf-8')).hexdigest()
        with open(filePath, "w") as f:
            f.write(self.data)
            if self.noise != '' :
                if re.match('[0-9]', self.data[-1]): #Well played dataset 0, well played.
                    f.write('\n')
                f.write(self.noise)
        return filePath
