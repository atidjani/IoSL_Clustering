import re
from django import forms
from django.utils.translation import ugettext as _
from django.core.exceptions import ValidationError

class UploadDatasetForm(forms.Form):
    file = forms.FileField(label='')
    algorithm = forms.ChoiceField([('STSC', 'STSC'), ('OPTICS', 'OPTICS')], widget=forms.RadioSelect(), label='')

class ParametersSTSC(forms.Form):
    numClusters = forms.IntegerField(required=True,min_value=1,label='# Clusters')
    k = forms.IntegerField(required=True,min_value=0)
    noiseFunctions = forms.CharField(required=False)
    generateNoise = forms.BooleanField(required=False)

    def clean(self):
        cleaned_data = super(ParametersSTSC, self).clean()
        fun = cleaned_data.get("noiseFunctions")

        if (fun != ''):
            print("Viva la figa")
            # Validate function field
            fun = re.sub('[\s+]', '', fun) #Remove spaces
            # DON'T FUCK UP WITH THIS LINE.
            reg = r"(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\))(\;(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\)))*"

            if not re.fullmatch(reg, fun) :
                self.add_error("noiseFunctions", "Format not correct")


