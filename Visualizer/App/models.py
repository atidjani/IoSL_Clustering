import io
import hashlib
import time

from django.db import models
from django.utils.translation import ugettext as _
from django.core.exceptions import ValidationError


# Create your models here.

class Dataset(models.Model):
    creationTime = models.DateTimeField(auto_now_add=True)
    data = models.TextField()
    noise = models.TextField(blank=True)

    def clean(self) :
        buffer = io.StringIO(self.data)
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

    def writeFile(self) :
        filePath = "/tmp/" + hashlib.md5(str(time.time()).encode('utf-8')).hexdigest()
        with open(filePath, "w") as f:
            f.write(self.data)
            f.write('\n' + self.noise)
        return filePath
