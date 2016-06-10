import re
from django import forms
from django.utils.translation import ugettext as _
from django.core.exceptions import ValidationError

class UploadDatasetForm(forms.Form):
    file = forms.FileField(label='')
    algorithm = forms.ChoiceField([('STSC', 'STSC'), ('OPTICSP', 'OPTICS - Python'), ('OPTICSR', 'OPTICS - R')], widget=forms.RadioSelect(), label='')

def fullmatch(regex, string):
    """Emulate python-3.4 re.fullmatch()."""
    return re.match("(?:" + regex + r")\Z", string)

class ParametersSTSC(forms.Form):
    numClusters = forms.IntegerField(required=True,min_value=1,label='# Clusters')
    k = forms.IntegerField(required=True,min_value=0)
    noiseFunctions = forms.CharField(required=False)
    generateNoise = forms.BooleanField(required=False)

    def clean(self):
        cleaned_data = super(ParametersSTSC, self).clean()
        fun = cleaned_data.get("noiseFunctions")

        if (fun != ''):
            # Validate function field
            fun = re.sub('[\s+]', '', fun) #Remove spaces
            # DON'T FUCK UP WITH THIS LINE.
            reg = r"(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\))(\;(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\)))*"

            if not fullmatch(reg, fun) :
                self.add_error("noiseFunctions", "Format not correct")

class ParametersOPTICSP(forms.Form) :
    minPoints = forms.IntegerField(required=True,min_value=1)
    eps = forms.DecimalField(required=True,min_value=0)
    noiseFunctions = forms.CharField(required=False)
    generateNoise = forms.BooleanField(required=False)

    def clean(self):
        cleaned_data = super(ParametersSTSC, self).clean()
        fun = cleaned_data.get("noiseFunctions")

        if (fun != ''):
            # Validate function field
            fun = re.sub('[\s+]', '', fun) #Remove spaces
            # DON'T FUCK UP WITH THIS LINE.
            reg = r"(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\))(\;(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\)))*"

            if not fullmatch(reg, fun) :
                self.add_error("noiseFunctions", "Format not correct")

class ParametersOPTICSR(forms.Form) :
    minPoints = forms.IntegerField(required=True,min_value=1)
    eps = forms.DecimalField(required=True,min_value=0)
    angle = forms.DecimalField(required=True)
    noiseFunctions = forms.CharField(required=False)
    generateNoise = forms.BooleanField(required=False)

    def clean(self):
        cleaned_data = super(ParametersSTSC, self).clean()
        fun = cleaned_data.get("noiseFunctions")

        if (fun != ''):
            # Validate function field
            fun = re.sub('[\s+]', '', fun) #Remove spaces
            # DON'T FUCK UP WITH THIS LINE.
            reg = r"(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\))(\;(\([x0-9\+\-\*\/\.]+,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+(\.[0-9]+)?,[0-9]+\)))*"

            if not fullmatch(reg, fun) :
                self.add_error("noiseFunctions", "Format not correct")


